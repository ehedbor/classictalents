from __future__ import annotations

import os
import re
import sys
import time

import selenium.webdriver.support.wait
from selenium import webdriver
from selenium.webdriver import ActionChains
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.chrome.service import Service as ChromeService
from selenium.webdriver.common.by import By
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.support.wait import WebDriverWait
from shutil import rmtree
from typing import List
from urllib.error import HTTPError
from urllib.parse import urlparse
from urllib.request import urlretrieve
from webdriver_manager.chrome import ChromeDriverManager
from webdriver_manager.firefox import GeckoDriverManager


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

    def scrape(self, spell_details):
        for entry in spell_details.find_elements(By.TAG_NAME, "tr"):
            if len(entry.find_elements(By.TAG_NAME, "th")) == 0:
                continue
            table_header = entry.find_element(By.TAG_NAME, "th").text
            table_data = entry.find_element(By.TAG_NAME, "td").text
            if table_header == "Cost":
                if table_data == "None":
                    self.cost = None
                else:
                    self.cost = table_data
                print(f"  Cost: {self.cost}")
            elif table_header == "Range":
                if "Self" in table_data:
                    self.range = None
                elif "5 yards" in table_data:
                    self.range = "Melee"
                else:
                    parts = table_data.split()
                    self.range = f"{parts[0]} {parts[1]}"
                print(f"  Range: {self.range}")
            elif table_header == "Cast time":
                self.cast_time = table_data
                print(f"  Cast Time: {self.cast_time}")
            elif table_header == "Cooldown":
                if table_data == "n/a":
                    self.cooldown = None
                else:
                    self.cooldown = table_data
                print(f"  Cooldown: {self.cooldown}")


class TalentModel:
    def __init__(self):
        self.row: int = 0
        self.column: int = 0
        self.max_rank: int = 0
        self.icon_url: str = ""
        self.name: str = ""
        self.description: str = ""
        self.prerequisite: str | None = None
        self.spell: SpellModel | None = None

    @property
    def icon(self):
        return url_to_file(self.icon_url)

    def scrape(self, driver, talent_element):
        self.row = int(talent_element.get_attribute("data-row"))
        self.column = int(talent_element.get_attribute("data-col"))
        print(f'     Talent ({self.row}, {self.column}):')

        self.max_rank = int(talent_element.get_attribute("data-max-points"))
        print(f'      Max Rank: {self.max_rank}')

        icon_element = talent_element.find_element(By.CSS_SELECTOR, ".iconmedium ins")
        self.icon_url = get_background_from_style_attr(icon_element)
        print(f'      Icon: "{self.icon_url}"')

        # scrolled in spec.scrape
        (ActionChains(driver)
            # .scroll_to_element(talent_element)
            .move_to_element(talent_element)
            .click()
            .pause(0.1)
            .perform())

        tooltip_path = "//*[contains(@class, 'wowhead-tooltip')]/table/tbody/tr/td"
        tooltip = driver.find_element(By.XPATH, tooltip_path)
        header = tooltip.find_elements(By.XPATH, "./table")[0]

        name_elem = header.find_elements(By.XPATH, "./tbody/tr/td/table/tbody/tr/td/a")
        if not name_elem:
            name_elem = header.find_elements(By.XPATH, "./tbody/tr/td/a")
        self.name = name_elem[0].text
        print(f'      Name: {self.name}')

        # todo scrape spell

        descriptions = []
        for rank in range(1, self.max_rank + 1):
            # we have to recalculate the tooltip because
            # clicking on the button makes the old tooltip stale
            tooltip = driver.find_element(By.XPATH, tooltip_path)
            desc_elem = tooltip.find_elements(By.XPATH, "./table")[1]
            desc = desc_elem.find_element(By.XPATH, "./tbody/tr/td/div[@class='q']").text
            print(f"      Rank {rank} description: '{desc}'")
            descriptions.append(desc)

            if rank != self.max_rank:
                # get next description
                ActionChains(driver).click().pause(0.25).perform()
        self.description = descriptions[0]

    # def finish_scrape(self, driver):
    #     driver.get(self.talent_url)
    #     self.name = driver.find_element(By.CSS_SELECTOR, "#main-contents .text h1").text
    #     print(f"Talent ({self.row}, {self.column}) = {self.name}")
    #
    #     # get spell data
    #     is_spell = True
    #     spell_details = driver.find_element(By.CSS_SELECTOR, "#spelldetails tbody")
    #     if spell_details.find_elements(By.XPATH, './/a[text() = "Passive spell"]'):
    #         is_spell = False
    #
    #     if is_spell:
    #         print(f" Spell:")
    #         self.spell = SpellModel()
    #         self.spell.scrape(spell_details)
    #     else:
    #         print(" Not spell")
    #
    #     # get description
    #     descriptions = []
    #     for rank in range(1, self.max_rank + 1):
    #         tooltip = driver.find_element(By.CSS_SELECTOR, "#main-contents .text .wowhead-tooltip")
    #         tooltip_content = tooltip.find_elements(By.CSS_SELECTOR, "table tbody tr td table")[-1]
    #         tooltip_desc = tooltip_content.find_element(By.CSS_SELECTOR, "tbody tr td div.q")
    #         descriptions.append(tooltip_desc.text)
    #
    #         if rank != self.max_rank:
    #             # TODO: not all talents (ie primal fury) have a see also tab
    #             see_also_button = driver.find_element(By.XPATH, "//a[contains(@href,'#see-also')]")
    #             see_also_button.click()
    #
    #             see_also_tab = driver.find_elements(By.XPATH,
    #                                                 '//*[contains(@id, "tab-see-also")]/'
    #                                                 'div[@class="listview-scroller-horizontal"]/'
    #                                                 'div[@class="listview-scroller-vertical"]/'
    #                                                 'table/tbody/tr')
    #             if len(see_also_tab) == 0:
    #                 raise RuntimeError("See also tab is empty!")
    #
    #             link = None
    #             for see_also_entry in see_also_tab:
    #                 name = see_also_entry.find_elements(By.TAG_NAME, "td")[1]
    #                 link_elem = name.find_element(By.CSS_SELECTOR, "div a")
    #                 talent_name = link_elem.text
    #                 if talent_name != self.name:
    #                     continue
    #
    #                 rank_text = name.find_element(By.CSS_SELECTOR, "div div.small2").text
    #                 if rank_text != f"Rank {rank + 1}":
    #                     continue
    #
    #                 link = link_elem.get_attribute("href")
    #
    #             if not link:
    #                 raise RuntimeError(
    #                     f"Can't find next rank link (next={rank + 1}/{self.max_rank})")
    #
    #             print(f"Next rank ({rank + 1}/{self.max_rank}): {link}")
    #             driver.get(link)
    #
    #     # TODO: merge descriptions
    #     self.description = descriptions[0]
    #     print(f' Descriptions:')
    #     for d in descriptions:
    #         print(f'  - "{d}"')

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

    def scrape(self, driver, tree_element):
        for e in tree_element.find_elements(By.XPATH, "./*"):
            print(e.tag_name, e.get_attribute("class"))
        self.name = tree_element.find_element(
            By.XPATH, "./div[@class='ctc-tree-header']/b").text
        print(f'   Spec {self.name}:')

        icon_element = tree_element.find_element(
            By.XPATH, "./div[@class='ctc-tree-header']/*[contains(@class, 'iconsmall')]/ins")
        self.icon_url = get_background_from_style_attr(icon_element) \
            .replace("icons/small", "icons/medium")
        print(f'    Icon: {self.icon_url}')

        # the background images are already downloaded, so don't download again.
        background_element = \
            tree_element.find_element(By.CSS_SELECTOR, ".ctc-tree-talents-background")
        self.background_url = get_background_from_style_attr(background_element)
        print(f'    Background: {self.background_url}')

        talents_path = "./*[@class='ctc-tree-talents']/*[@class='ctc-tree-talent']"
        talents = tree_element.find_elements(By.XPATH, talents_path)
        
        def key_function(tup):
            index, talent = tup
            row = int(talent.get_attribute("data-row"))
            column = int(talent.get_attribute("data-col"))
            return 4 * row + column

        talents = sorted(enumerate(talents), key=key_function)

        print(f"Iterating over talents: (len={len(talents)})")
        for i, talent_element in talents:
            talent_model = TalentModel()

            # talent_element_path = f"{talents_path}[{i + 1}]"
            # WebDriverWait(tree_element, 20).until(
            #     ec.visibility_of_element_located((By.XPATH, talent_element_path)))
            # driver.execute_script("return arguments[0].scrollIntoView(true);", talent_element)

            # deallocate old talents when we are potentially going to run out of points
            points_left = int(driver.find_element(
                By.CSS_SELECTOR, ".ctc-main-status-points-spent b").text)
            if points_left <= 5:
                print("Deallocating old talents")
                # don't modify the 5 most recent talents
                for _, t in talents[:-5]:
                    # remove all points from this talent
                    while (current_points := int(t.get_attribute('data-points'))) > 0:
                        ActionChains(driver).context_click(t).perform()
                        if int(t.get_attribute('data-points')) == current_points:
                            # cant be deallocated
                            break

                    points_left = int(driver.find_element(
                        By.CSS_SELECTOR, ".ctc-main-status-points-spent b").text)
                    if points_left >= 10:
                        break

            talent_model.scrape(driver, talent_element)
            self.talents.append(talent_model)

        # reset talents once done with the spec
        print("Resetting spec talents")

        reset_button_path = ".//span[contains(@class, 'ctc-tree-header-reset')]"
        reset_button = tree_element.find_element(By.XPATH, reset_button_path)
        # WebDriverWait(driver, 20).until(
        #     ec.visibility_of_element_located((By.XPATH, reset_button_path)))
        # driver.execute_script("return arguments[0].scrollIntoView(true);", reset_button)

        (ActionChains(driver)
            .move_to_element(reset_button)
            .click(reset_button)
            .perform())

    def download_images(self, output_dir: str):
        download_file(self.icon_url, f"{output_dir}/{self.icon}")
        download_file(self.background_url, f"{output_dir}/{self.background}")


class ExpacModel:
    def __init__(self):
        self.name: str = ""
        self.specializations: List[SpecModel] = []

    def scrape(self, driver, expansion, class_name):
        print(f" Expansion {expansion}:")
        url = get_wowhead_talent_calc_url(expansion, class_name)
        print(f'  Url: "{url}"')
        driver.get(url)

        # scroll down so that all talents are visible
        header_path = ".//div[contains(@class, 'ctc-main-status-class-name')]"
        header = driver.find_element(By.XPATH, header_path)
        WebDriverWait(driver, 20).until(
            ec.visibility_of_element_located((By.XPATH, header_path)))
        driver.execute_script("return arguments[0].scrollIntoView(true);", header)

        self.name = expansion
        self.specializations = []

        # iterate weirdly to avoid stale references
        num_specs = len(driver.find_elements(By.XPATH, '//*[@class="ctc-tree"]'))
        for i in range(num_specs):
            spec_model = SpecModel()
            print(f"Spec #{i + 1}/{num_specs}")
            tree = driver.find_elements(By.XPATH, f'//*[@class="ctc-tree"]')[i]
            spec_model.scrape(driver, tree)
            self.specializations.append(spec_model)

class ClassModel:
    def __init__(self):
        self.name: str = ""
        self.icon_url: str = ""
        self.color: str = ""
        self.expansions: List[ExpacModel] = []

    def scrape(self, driver, class_name, expansions):
        if len(expansions) == 0:
            return None

        print(f"Scraping class {class_name}...")

        for expansion in expansions:
            expac_model = ExpacModel()
            expac_model.scrape(driver, expansion, class_name)
            self.expansions.append(expac_model)

        # use the currently open page to get info about the class as a whole
        status_bar = driver.find_element(By.CSS_SELECTOR, ".ctc-main-status-class-name")
        self.icon_url = status_bar.find_element(By.CSS_SELECTOR, "img").get_attribute("src")
        name_element = status_bar.find_element(By.CSS_SELECTOR, ".ctc-main-status-class-name-name")
        self.name = name_element.text[:-1]
        self.color = name_element.value_of_css_property("color")

        print(f' Icon: "{self.icon_url}"')
        print(f' Name: "{self.name}"')
        print(f' Color: "{self.color}"')

    @property
    def icon(self):
        return url_to_file(self.icon_url)

    def download_images(self, output_dir: str):
        download_file(self.icon_url, f"{output_dir}/{self.icon}")

def scrape(driver):
    # close cookie warning
    driver.get("https://classic.wowhead.com")

    reject_cookies_path = "//button[@id='onetrust-reject-all-handler']"
    WebDriverWait(driver, 20).until(ec.element_to_be_clickable((By.XPATH, reject_cookies_path)))
    time.sleep(0.5)
    reject_cookies = driver.find_element(By.XPATH, reject_cookies_path)
    reject_cookies.click()

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

    classes = []
    for class_name, expansions in classes_to_scrape:
        model = ClassModel()
        model.scrape(driver, class_name, expansions)
        classes.append(model)

    # for class_model in classes:
    #     for expac_model in class_model.expansions:
    #         for spec_model in expac_model.specializations:
    #             for talent_model in spec_model.talents:
    #                 talent_model.finish_scrape(driver)

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
    use_firefox = True
    if use_firefox:
        service = FirefoxService(executable_path=GeckoDriverManager("v0.31.0").install())
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
