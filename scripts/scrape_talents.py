from __future__ import annotations

import os
import re
import sys

from selenium import webdriver
from selenium.webdriver import ActionChains
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.common.by import By
from selenium.webdriver.firefox.service import Service as FirefoxService
from shutil import rmtree
from typing import List
from urllib.error import HTTPError
from urllib.parse import urlparse
from urllib.request import urlretrieve
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager


def download_file(url, path):
    print(f'Downloading "{url}" to "{path}" ... ', end='')
    os.makedirs(os.path.dirname(path), exist_ok=True)

    try:
        if not os.path.exists(path):
            urlretrieve(url, path)
            print("OK")
        else:
            print("Skipped")
    except HTTPError as e:
        print(f'ERROR: {e.code} {e.reason}', file=sys.stderr)


def url_to_file(url):
    """
    Extracts and returns the file component of a URL
    """
    filename = os.path.basename(urlparse(url).path)
    return filename.replace("_", "/").replace("-", "/")


class SpellModel:
    def __init__(self):
        self.cast_time: str | None = None
        self.range: str | None = None
        self.cooldown: str | None = None
        self.cost: str | None = None


class TalentModel:
    def __init__(self):
        self.row: int = 0
        self.column: int = 0
        self.max_rank: int = 0
        self.icon_url: str = ""
        self.talent_url: str = ""
        self.name: str = ""
        self.description: str = ""
        self.prerequisite: str | None = None
        self.spell: SpellModel | None = None

    @property
    def icon(self):
        return url_to_file(self.icon_url)

    def finish_scrape(self, driver):
        driver.get(self.talent_url)
        self.name = driver.find_element(By.CSS_SELECTOR, "#main-contents .text h1").text
        print(f"Talent ({self.row}, {self.column}) = {self.name}")

        # get spell data                   
        is_spell = True
        spell_details = driver.find_element(By.CSS_SELECTOR, "#spelldetails tbody")
        # passive spell link
        if spell_details.find_elements(By.XPATH, './/a[text() = "Passive spell"]'):
            is_spell = False
        # for entry in spell_details.find_elements(By.TAG_NAME, "tr"):
        #     header = entry.find_elements(By.TAG_NAME, "th")
        #     if len(header) != 1:
        #         continue
        #
        #     if header[0].text == "Flags":
        #         for flag in entry.find_elements(By.CSS_SELECTOR, "td ul li"):
        #             print(flag.text)
        #             if len(flag.find_elements(By.XPATH, "./a[text() = 'Passive spell']")) >= 1:
        #                 is_spell = False
        #                 break
        #         break

        if is_spell:
            print(f" Spell:")
            self.spell = SpellModel()
            for entry in spell_details.find_elements(By.TAG_NAME, "tr"):
                if len(entry.find_elements(By.TAG_NAME, "th")) == 0:
                    continue
                table_header = entry.find_element(By.TAG_NAME, "th").text
                table_data = entry.find_element(By.TAG_NAME, "td").text
                if table_header == "Cost":
                    if table_data == "None":
                        self.spell.cost = None
                    else:
                        self.spell.cost = table_data
                    print(f"  Cost: {self.spell.cost}")
                elif table_header == "Range":
                    if "Self" in table_data:
                        self.spell.range = None
                    elif "5 yards" in table_data:
                        self.spell.range = "Melee"
                    else:
                        parts = table_data.split()
                        self.spell.range = f"{parts[0]} {parts[1]}"
                    print(f"  Range: {self.spell.range}")
                elif table_header == "Cast time":
                    self.spell.cast_time = table_data
                    print(f"  Cast Time: {self.spell.cast_time}")
                elif table_header == "Cooldown":
                    if table_data == "n/a":
                        self.spell.cooldown = None
                    else:
                        self.spell.cooldown = table_data
                    print(f"  Cooldown: {self.spell.cooldown}")
        else:
            print(" Not spell")
                    
        descriptions = []
        for rank in range(1, self.max_rank + 1):
            tooltip = driver.find_element(By.CSS_SELECTOR, "#main-contents .text .wowhead-tooltip")
            tooltip_content = tooltip.find_elements(By.CSS_SELECTOR, "table tbody tr td table")[-1]
            tooltip_desc = tooltip_content.find_element(By.CSS_SELECTOR, "tbody tr td div.q")
            descriptions.append(tooltip_desc.text)

            if rank != self.max_rank:
                close_cookies = \
                    driver.find_elements(By.XPATH, "//button[@id='onetrust-reject-all-handler']")
                if len(close_cookies) == 1:
                    close_cookies[0].click()

                see_also_button = driver.find_element(By.XPATH, "//a[contains(@href,'#see-also')]")
                ActionChains(driver)\
                    .scroll_to_element(see_also_button)\
                    .pause(0.01)\
                    .click(see_also_button)\
                    .pause(0.01)\
                    .perform()

                see_also = driver.find_elements(By.CSS_SELECTOR, "#tab-see-also-ability"
                                                                 " .listview-scroller-horizontal"
                                                                 " .listview-scroller-vertical"
                                                                 " table tbody tr")

                link = None
                for see_also_entry in see_also:
                    name = see_also_entry.find_elements(By.TAG_NAME, "td")[1]
                    link_elem = name.find_element(By.CSS_SELECTOR, "div a")
                    talent_name = link_elem.text
                    if talent_name != self.name:
                        continue

                    rank_text = name.find_element(By.CSS_SELECTOR, "div div.small2").text
                    if rank_text != f"Rank {rank + 1}":
                        continue

                    link = link_elem.get_attribute("href")

                if not link:
                    raise RuntimeError(
                        f"Can't find next rank link (next={rank + 1}/{self.max_rank})")

                print(f"Next rank ({rank + 1}/{self.max_rank}): {link}")
                driver.get(link)

        # TODO: merge descriptions
        self.description = descriptions[0]
        print(f' Descriptions:')
        for d in descriptions:
            print(f'  - "{d}"')

    def download_images(self, output_dir: str):
        download_file(self.icon_url, f"{output_dir}/{self.icon}")


class SpecModel:
    def __init__(self):
        self.name: str = ""
        self.icon_url: str = ""
        self.background_url: str = ""
        self.talents: List[TalentModel] = []

    @property
    def icon(self):
        return url_to_file(self.icon_url)

    @property
    def background(self):
        return "backgrounds/" + url_to_file(self.background_url)

    def download_images(self, output_dir: str):
        download_file(self.icon_url, f"{output_dir}/{self.icon}")
        download_file(self.background_url, f"{output_dir}/{self.background}")


class ExpacModel:
    def __init__(self):
        self.name: str = ""
        self.specializations: List[SpecModel] = []


class ClassModel:
    def __init__(self):
        self.name: str = ""
        self.icon_url: str = ""
        self.color: str = ""
        self.expansions: List[ExpacModel] = []

    @property
    def icon(self):
        return url_to_file(self.icon_url)

    def download_images(self, output_dir: str):
        download_file(self.icon_url, f"{output_dir}/{self.icon}")


def get_wowhead_talent_calc_url(expansion, class_name):
    if expansion in ['classic', 'tbc']:
        return f"https://{expansion}.wowhead.com/talent-calc/{class_name}"
    else:  # wotlk
        return f"https://www.wowhead.com/wotlk/talent-calc/{class_name}"


URL_FROM_STYLE_REGEX = re.compile('background-image: url\\("([^"]+)"\\);')

def get_background_from_style_attr(element):
    style = element.get_attribute("style")
    match = re.match(URL_FROM_STYLE_REGEX, style)
    return match.group(1)


def scrape_talent(talent):
    model = TalentModel()

    model.row = int(talent.get_attribute("data-row"))
    model.column = int(talent.get_attribute("data-col"))
    print(f'     Talent ({model.row}, {model.column}):')

    model.max_rank = int(talent.get_attribute("data-max-points"))
    print(f'      Max Rank: {model.max_rank}')

    icon_element = talent.find_element(By.CSS_SELECTOR, ".iconmedium ins")
    model.icon_url = get_background_from_style_attr(icon_element)
    print(f'      Icon: "{model.icon_url}"')

    model.talent_url = talent.find_element(By.CSS_SELECTOR, ".iconmedium a").get_attribute("href")
    print(f'      Wowhead Spell URL: "{model.talent_url}"')

    return model

def scrape_spec(talent_tree):
    model = SpecModel()

    model.name = talent_tree.find_element(By.CSS_SELECTOR, ".ctc-tree-header b").text
    print(f'   Spec {model.name}:')

    icon_element = talent_tree.find_element(By.CSS_SELECTOR, ".iconsmall ins")
    model.icon_url = get_background_from_style_attr(icon_element) \
        .replace("icons/small", "icons/medium")
    print(f'    Icon: {model.icon_url}')

    # the background images are already downloaded, so don't download again.
    background_element = \
        talent_tree.find_element(By.CSS_SELECTOR, ".ctc-tree-talents-background")
    model.background_url = get_background_from_style_attr(background_element)
    print(f'    Background: {model.background_url}')

    talents = talent_tree.find_elements(By.CSS_SELECTOR, ".ctc-tree-talents .ctc-tree-talent")

    model.talents = [scrape_talent(t) for t in talents]

    return model


def scrape_class(driver, class_name, expansions):
    if len(expansions) == 0:
        return None

    print(f"Scraping class {class_name}...")

    model = ClassModel()
    model.name = class_name
    model.icon_url = ""
    model.color = ""

    for expansion in expansions:
        print(f" Expansion {expansion}:")
        url = get_wowhead_talent_calc_url(expansion, class_name)
        print(f'  Url: "{url}"')
        driver.get(url)
        trees = driver.find_elements(By.CSS_SELECTOR, ".ctc-tree")

        expac_model = ExpacModel()
        expac_model.name = expansion
        expac_model.specializations = [scrape_spec(talent_tree) for talent_tree in trees]
        if expansion == 'classic':
            model.classic = expac_model
        elif expansion == 'tbc':
            model.tbc = expac_model
        elif expansion == 'wotlk':
            model.wotlk = expac_model

        model.expansions.append(expac_model)

    # use the currently open page to get info about the class as a whole
    status_bar = driver.find_element(By.CSS_SELECTOR, ".ctc-main-status-class-name")
    model.icon_url = status_bar.find_element(By.CSS_SELECTOR, "img").get_attribute("src")
    name_element = status_bar.find_element(By.CSS_SELECTOR, ".ctc-main-status-class-name-name")
    model.name = name_element.text[:-1]
    model.color = name_element.value_of_css_property("color")

    print(f' Icon: "{model.icon_url}"')
    print(f' Name: "{model.name}"')
    print(f' Color: "{model.color}"')

    return model


def scrape(driver):
    classes_to_scrape = [
        ('druid', ['classic'])
        # ('druid',        ['classic', 'tbc', 'wotlk']),
        # ('hunter',       ['classic', 'tbc', 'wotlk']),
        # ('mage',         ['classic', 'tbc', 'wotlk']),
        # ('paladin',      ['classic', 'tbc', 'wotlk']),
        # ('priest',       ['classic', 'tbc', 'wotlk']),
        # ('rogue',        ['classic', 'tbc', 'wotlk']),
        # ('shaman',       ['classic', 'tbc', 'wotlk']),
        # ('warlock',      ['classic', 'tbc', 'wotlk']),
        # ('warrior',      ['classic', 'tbc', 'wotlk']),
        # ('death-knight', ['wotlk']),
    ]

    classes = [scrape_class(driver, class_name, expansions)
               for class_name, expansions in classes_to_scrape]

    for class_model in classes:
        for expac_model in class_model.expansions:
            for spec_model in expac_model.specializations:
                for talent_model in spec_model.talents:
                    talent_model.finish_scrape(driver)

    return classes

def download_images(classes: List[ClassModel]):
    print("Downloading image files...")
    # remote the output dir if it already exists
    # rmtree('out')
    os.makedirs('out', exist_ok=True)
    for class_model in classes:
        # output_dir = "out/" + class_model.name.lower().replace(' ', '')
        output_dir = "out/images"
        os.makedirs(output_dir, exist_ok=True)
        print(f'Class {class_model.name} -> "{output_dir}"')
        # class_model.download_images(output_dir)

        for expac_model in class_model.expansions:
            print(f' Expac {expac_model.name}:')
            for spec_model in expac_model.specializations:
                print(f'  Spec {spec_model.name} -> "{output_dir}"')
                # spec_output_dir = output_dir + "/" + spec_model.name.lower().replace(' ', '')
                spec_output_dir = output_dir
                os.makedirs(spec_output_dir, exist_ok=True)
                spec_model.download_images(spec_output_dir)

                for talent_model in spec_model.talents:
                    talent_model.download_images(spec_output_dir)

def main():
    use_firefox = False
    if use_firefox:
        service = FirefoxService(executable_path=GeckoDriverManager().install())
        driver = webdriver.Firefox(service=service)
    else:
        service = ChromeService(executable_path=ChromeDriverManager().install())
        options = ChromeOptions()
        options.headless = True
        driver = webdriver.Chrome(service=service, options=options)

    classes = scrape(driver)
    driver.quit()

    download_images(classes)

main()
