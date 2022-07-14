from __future__ import annotations
import os
import re
import sys

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.firefox.service import Service as FirefoxService
from shutil import rmtree
from typing import List
from urllib.error import HTTPError
from urllib.parse import urlparse
from urllib.request import urlretrieve
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

class SpellModel:
    def __init__(self):
        pass


class TalentModel:
    def __init__(self):
        self.row: int = 0
        self.column: int = 0
        self.max_rank: int = 0
        self.icon_url: str = ""
        self.talent_url: str = ""

    def finish_scrape(self, driver):
        pass

    def download_images(self, output_dir: str):
        icon_file = os.path.basename(urlparse(self.icon_url).path).replace("_", "/")
        download_file(self.icon_url, f"{output_dir}/{icon_file}")


class SpecModel:
    def __init__(self):
        self.name: str = ""
        self.icon_url: str = ""
        self.background_url: str = ""
        self.talents: List[TalentModel] = []

    def download_images(self, output_dir: str):
        icon_file = os.path.basename(urlparse(self.icon_url).path).replace("_", "/")
        download_file(self.icon_url, f"{output_dir}/{icon_file}")

        bg_file = os.path.basename(urlparse(self.background_url).path).replace("_", "/")
        download_file(self.background_url, f"{output_dir}/backgrounds/{bg_file}")


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

    def download_images(self, output_dir: str):
        icon_file = os.path.basename(urlparse(self.icon_url).path).replace("_", "/")
        download_file(self.icon_url, f"{output_dir}/{icon_file}")


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
    print(f'    Talent ({model.row}, {model.column}):')

    model.max_rank = int(talent.get_attribute("data-max-points"))
    print(f'     Max Rank: {model.max_rank}')

    icon_element = talent.find_element(By.CSS_SELECTOR, ".iconmedium ins")
    model.icon_url = get_background_from_style_attr(icon_element)
    print(f'     Icon: "{model.icon_url}"')

    model.talent_url = talent.find_element(By.CSS_SELECTOR, ".iconmedium a").get_attribute("href")
    print(f'     Wowhead Spell URL: "{model.talent_url}"')

    return model

def scrape_spec(talent_tree):
    model = SpecModel()

    model.name = talent_tree.find_element(By.CSS_SELECTOR, ".ctc-tree-header b").text
    print(f'  Spec {model.name}:')

    icon_element = talent_tree.find_element(By.CSS_SELECTOR, ".iconsmall ins")
    model.icon_url = get_background_from_style_attr(icon_element) \
        .replace("icons/small", "icons/medium")
    print(f'   Icon: {model.icon_url}')

    # the background images are already downloaded, so don't download again.
    background_element = \
        talent_tree.find_element(By.CSS_SELECTOR, ".ctc-tree-talents-background")
    model.background_url = get_background_from_style_attr(background_element)
    print(f'   Background: {model.background_url}')

    talents = talent_tree.find_elements(By.CSS_SELECTOR, ".ctc-tree-talents .ctc-tree-talent")

    model.talents = [scrape_talent(t) for t in talents]

    return model


def scrape_class(driver, class_name, expansions):
    print(f"Scraping class {class_name}...")

    model = ClassModel()
    model.name = class_name
    model.icon_url = ""
    model.color = ""

    for expansion in expansions:
        print(f" Expansion {expansion}:")
        url = get_wowhead_talent_calc_url(expansion, class_name)
        print(f' Url: "{url}"')
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

    return model


def scrape(driver):
    classes_to_scrape = [
        # ('druid',       ['classic', 'tbc', 'wotlk']),
        # ('hunter',      ['classic', 'tbc', 'wotlk']),
        # ('mage',        ['classic', 'tbc', 'wotlk']),
        # ('paladin',     ['classic', 'tbc', 'wotlk']),
        # ('priest',      ['classic', 'tbc', 'wotlk']),
        # ('rogue',       ['classic', 'tbc', 'wotlk']),
        # ('shaman',      ['classic', 'tbc', 'wotlk']),
        # ('warlock',     ['classic', 'tbc', 'wotlk']),
        # ('warrior',     ['classic', 'tbc', 'wotlk']),
        ('death-knight', ['wotlk']),
    ]

    classes = [scrape_class(driver, class_name, expansions)
               for class_name, expansions in classes_to_scrape]
    # classes = [scrape_class(driver, 'druid', ['classic', 'tbc', 'wotlk'])]

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
    service = FirefoxService(executable_path=GeckoDriverManager().install())
    driver = webdriver.Firefox(service=service)
    classes = scrape(driver)
    driver.quit()

    download_images(classes)

main()
