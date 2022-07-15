from __future__ import annotations

import os
import random
import re
import sys
import time

from scrape_talents_util import merge_talent_desc
from selenium.common.exceptions import StaleElementReferenceException
from selenium.webdriver import ActionChains, Firefox
from selenium.webdriver.common.by import By
from selenium.webdriver.firefox.service import Service as FirefoxService
from selenium.webdriver.firefox.options import Options as FirefoxOptions
from selenium.webdriver.support import expected_conditions as ec
from selenium.webdriver.support.wait import WebDriverWait
from typing import List
from urllib.error import HTTPError
from urllib.parse import urlparse
from urllib.request import urlretrieve
from webdriver_manager.firefox import GeckoDriverManager
import yaml


CLICK_DELAY = 0.4
DEALLOC_THRESHOLD = 5
FINISH_DEALLOC_THRESHOLD = 4
FALLBACK_IMAGE_URL = "https://wow.zamimg.com/images/wow/icons/medium/inv_misc_questionmark.jpg"


def get_wowhead_talent_calc_url(expansion, class_name):
    if expansion in ['classic', 'tbc']:
        return f"https://{expansion}.wowhead.com/talent-calc/{class_name}"
    else:  # wotlk
        return f"https://www.wowhead.com/wotlk/talent-calc/{class_name}"


def get_background_from_style_attr(element):
    style = element.get_attribute("style")
    match = re.match('background-image: url\\("([^"]+)"\\);', style)
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
        return url
    except HTTPError as e:
        print(f'ERROR: {e.code} {e.reason}', file=sys.stderr)
        urlretrieve(FALLBACK_IMAGE_URL, path)
        return FALLBACK_IMAGE_URL


def url_to_file(url):
    """
    Extracts and returns the file component of a URL
    """
    filename = os.path.basename(urlparse(url).path)

    return filename.replace("_", "/").replace("-", "/")

class Location:
    """
    Used for YAML output
    """
    def __init__(self, row, column):
        self.row = row
        self.column = column

def represent_location(dumper: yaml.Dumper, data: Location) -> yaml.Node:
    return dumper.represent_sequence('tag:yaml.org,2002:seq', [data.row, data.column], True)


class SpellModel:
    def __init__(self):
        """
        spell_info format:
        unit range
        cast_time cooldown
        """
        self.cast_time: str | None = None
        self.range: str | None = None
        self.cooldown: str | None = None
        self.cost: str | None = None

    @staticmethod
    def from_spell_info(spell_info: List[str]) -> SpellModel | None:
        spell = SpellModel()
        print(f'      Spell:')

        spell_info = spell_info.copy()
        i = 0
        while i < len(spell_info):
            e = spell_info[i]
            if 'Rank' in e or 'Talent' in e or 'Requires' in e or 'Level' in e:
                del spell_info[i]
                continue
            i += 1

        if len(spell_info) == 0:
            return None
        elif len(spell_info) == 1:
            spell_info.insert(0, "")

        # '# yd range', '# - # yd range' or 'melee range'
        parts = spell_info[0].split()
        if len(parts) > 0 and parts[-1].lower() == 'range':
            parts.pop()
            if parts[-1].lower() == "melee":
                spell.range = "Melee"
                parts.pop()
            else:
                spell.range = parts.pop(-2) + " " + parts.pop(-1)
                if parts and parts[-1] == "-":
                    spell.range = f"{parts.pop(-2)} {parts.pop(-1)} {spell.range}"

            print(f'       Range: {spell.range}')

        # next is unit. this can be used directly
        if len(parts) > 0:
            spell.cost = ' '.join(parts)
            print(f'       Cost: {spell.cost}')

        parts = spell_info[1].split()
        # cooldown
        if len(parts) > 0 and parts[-1].lower() == "cooldown":
            parts.pop()
            spell.cooldown = parts.pop(-2) + " " + parts.pop(-1)
            print(f'       CD: {spell.cooldown}')

        # cast time can be used directly, unless it ends in 'cast'
        if len(parts) > 0 and parts[-1].lower() == "cast":
            parts.pop()
            spell.cast_time = ' '.join(parts)
        else:
            spell.cast_time = 'Instant'
        print(f'       Cast: {spell.cast_time}')

        return spell

    def generate_yaml(self):
        result = {}

        if self.cost:
            result['Cost'] = self.cost
        if self.range:
            result['Range'] = self.range
        result['Cast Time'] = self.cast_time
        if self.cooldown:
            result['Cooldown'] = self.cooldown

        return result


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
        self._scrape_basics(talent_element)

        # allocate a single point. this is needed for description later on
        
        (ActionChains(driver)
            .move_to_element(talent_element)
            .click()
            .pause(CLICK_DELAY)
            .perform())

        tooltip_path = "//*[contains(@class, 'wowhead-tooltip')]/table/tbody/tr/td"
        header_path = f"{tooltip_path}/table[1]"

        self._scrape_name(driver, header_path)

        while True:
            try:
                spell_info = driver.find_element(By.XPATH, f"{header_path}/tbody/tr/td")\
                                   .text.splitlines()
                break
            except StaleElementReferenceException:
                print("Spell info stale, retrying...")

        self.spell = SpellModel.from_spell_info(spell_info)

        self._scrape_description(driver, tooltip_path)

    def _scrape_basics(self, talent_element):
        self.row = int(talent_element.get_attribute("data-row"))
        self.column = int(talent_element.get_attribute("data-col"))
        print(f'     Talent ({self.row}, {self.column}):')

        self.max_rank = int(talent_element.get_attribute("data-max-points"))
        print(f'      Max Rank: {self.max_rank}')

        icon_element = talent_element.find_element(By.CSS_SELECTOR, ".iconmedium ins")
        self.icon_url = get_background_from_style_attr(icon_element)
        print(f'      Icon: "{self.icon_url}"')

    def _scrape_name(self, driver, header_path):
        while True:
            try:
                WebDriverWait(driver, 20).until(
                    ec.presence_of_element_located((By.XPATH, header_path)))
                header = driver.find_element(By.XPATH, header_path)

                name_elem = header.find_elements(By.XPATH, ".//tbody/tr/td/a/b")
                if len(name_elem) == 1:
                    break
            except StaleElementReferenceException:
                print('Stale element when getting talent name, retrying...', file=sys.stderr)

        if len(name_elem) == 0:
            raise RuntimeError("Failed to get name after 10 attempts")

        self.name = name_elem[0].text
        print(f'      Name: {self.name}')

    def _scrape_description(self, driver, tooltip_path):
        descriptions = []
        for rank in range(1, self.max_rank + 1):
            # we have to recalculate the tooltip because
            # clicking on the button makes the old tooltip stale
            while True:
                try:
                    desc_path = f"{tooltip_path}/table[2]/tbody/tr/td/div[@class='q']"
                    WebDriverWait(driver, 20).until(
                        ec.presence_of_element_located((By.XPATH, desc_path)))
                    # this can be stale for some reason, so keep trying till it isnt
                    desc = driver.find_element(By.XPATH, desc_path).text
                    break
                except StaleElementReferenceException:
                    print('Stale element when getting talent description, retrying...',
                          file=sys.stderr)

            descriptions.append(desc)

            if rank != self.max_rank:
                # get next description
                ActionChains(driver).click().pause(CLICK_DELAY).perform()

        self.description = merge_talent_desc(descriptions)
        print(f'      Description: "{self.description}"')

    def download_images(self, output_dir: str):
        self.icon_url = download_file(self.icon_url, f"{output_dir}/{self.icon}")

    def generate_yaml(self):
        result = {
            'Location': Location(self.row, self.column),
            'Requires': self.prerequisite,
            'Max Rank': self.max_rank,
            'Icon': self.icon,
            'Description': self.description,
        }
        if not self.prerequisite:
            del result['Requires']
        if self.spell:
            result['Spell'] = self.spell.generate_yaml()

        return result


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
        self._scrape_simple(tree_element)
        self._scrape_talents(driver, tree_element)
        self._reset_talents(driver, tree_element)

    def _scrape_simple(self, tree_element):
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

    def _scrape_talents(self, driver, tree_element):
        talents_path = "./*[@class='ctc-tree-talents']/*[@class='ctc-tree-talent']"
        talents = tree_element.find_elements(By.XPATH, talents_path)
        prerequisites = self._scrape_prerequisites(tree_element)

        def key_function(talent):
            row = int(talent.get_attribute("data-row"))
            column = int(talent.get_attribute("data-col"))
            # misnomer. higher priority => sorted later
            priority = 15 * row + column
            # ensure that talents with prerequisites are last in their row
            if (row, column) in prerequisites.values():
                priority += 10
                # if this is also a dependency, make it happen before other prereqs
                if (row, column) in prerequisites.keys():
                    priority -= 5
            return priority

        talents = sorted(talents, key=key_function)

        dealloced_talents = []
        for talent_element in talents:
            self._deallocate_old_talents(driver, talents, dealloced_talents)
            talent_model = TalentModel()
            talent_model.scrape(driver, talent_element)
            self.talents.append(talent_model)

        print("    Resolving prerequisites...")
        for prereq_loc, depend_loc in prerequisites.items():
            prerequisite = next(t for t in self.talents if prereq_loc == (t.row, t.column))
            dependency = next(t for t in self.talents if depend_loc == (t.row, t.column))
            print(f"     {prerequisite.name} unlocks {dependency.name}")
            dependency.prerequisite = prerequisite.name

    def _scrape_prerequisites(self, tree_element):
        def zero(a): return 0
        def get_size(a): return int(a.get_attribute('data-size'))
        def get_width(a): return int(a.get_attribute('data-width'))
        def get_height(a): return int(a.get_attribute('data-height'))
        def negate(func): return lambda a: -func(a)
        arrow_types = [
            ('ctc-tree-talent-arrow-down',       zero,              get_size),
            ('ctc-tree-talent-arrow-right',      get_size,          zero),
            ('ctc-tree-talent-arrow-right-down', get_width,         get_height),
            ('ctc-tree-talent-arrow-left',       negate(get_size),  zero),
            ('ctc-tree-talent-arrow-left-down',  negate(get_width), get_height),
        ]

        print("    Scraping prerequisites...")
        prerequisites = {}
        for css_class, horiz_offset, vert_offset in arrow_types:
            arrows = tree_element.find_elements(By.CLASS_NAME, css_class)
            for arrow in arrows:
                prereq_row = int(arrow.get_attribute('data-row'))
                prereq_col = int(arrow.get_attribute('data-col'))
                dep_row = prereq_row + vert_offset(arrow)
                dep_col = prereq_col + horiz_offset(arrow)
                prerequisites[(prereq_row, prereq_col)] = (dep_row, dep_col)
                print(f"     Talent ({prereq_row}, {prereq_col}) unlocks"
                      f" talent ({dep_row}, {dep_col})")

        return prerequisites

    def _deallocate_old_talents(self, driver, talents, dealloced_talents):
        points_left = int(driver.find_element(
            By.CSS_SELECTOR, ".ctc-main-status-points-spent b").text)
        if points_left >= DEALLOC_THRESHOLD:
            return

        # deallocate old talents when we are potentially going to run out of points
        # but don't modify the 5 most recent talents
        for i, t in enumerate(talents[:-5]):
            if i in dealloced_talents:
                continue

            # remove all points from this talent
            old_points = int(t.get_attribute('data-points'))
            while True:
                if old_points == 0:
                    dealloced_talents.append(i)
                    break

                ActionChains(driver).context_click(t).perform()
                new_points = int(t.get_attribute('data-points'))

                if old_points == new_points:
                    # can't be deallocated
                    dealloced_talents.append(i)
                    break

                old_points = new_points

            points_left = int(driver.find_element(
                By.CSS_SELECTOR, ".ctc-main-status-points-spent b").text)
            if points_left > FINISH_DEALLOC_THRESHOLD:
                break

    def _reset_talents(self, driver, tree_element):
        # reset talents once done with the spec
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
        self.icon_url = download_file(self.icon_url, f"{output_dir}/{self.icon}")
        self.background_url = download_file(self.background_url, f"{output_dir}/{self.background}")
        for talent in self.talents:
            talent.download_images(output_dir)

    def generate_yaml(self):
        result = {
            'Icon': self.icon,
            'Background': self.background,
            'Talents': {}
        }

        for talent in self.talents:
            result['Talents'][talent.name] = talent.generate_yaml()

        return result


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
            tree = driver.find_elements(By.XPATH, f'//*[@class="ctc-tree"]')[i]
            spec_model.scrape(driver, tree)
            self.specializations.append(spec_model)

    def download_images(self, output_dir: str):
        for spec in self.specializations:
            spec.download_images(output_dir)

    def generate_yaml(self):
        result = {'Specializations': {}}
        for spec in sorted(self.specializations, key=lambda s: s.name):
            result['Specializations'][spec.name] = spec.generate_yaml()
        return result

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
            failures = 0
            while True:
                try:
                    expac_model = ExpacModel()
                    expac_model.scrape(driver, expansion, class_name)
                    self.expansions.append(expac_model)
                    break
                except BaseException as e:
                    failures += 1
                    if failures == 3:
                        print(f"FATAL ERROR! Expansion {expansion} FAILED for {class_name}"
                              " after 3 attempts!")
                        raise
                    else:
                        print(f"SEVERE ERROR: Expansion {expansion} FAILED for {class_name}!",
                              file=sys.stderr)
                        print(f"    Cause: {e}")
                        print("Retrying...")

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
        self.icon_url = download_file(self.icon_url, f"{output_dir}/{self.icon}")
        for expac in self.expansions:
            expac.download_images(output_dir)

    def generate_yaml(self):
        cls = {
            "Class": self.name,
            "Icon": self.icon,
            "Color": self.color,
        }
        for e in self.expansions:
            if e.name == 'classic':
                cls['Classic'] = e.generate_yaml()
            elif e.name == 'tbc':
                cls['TBC'] = e.generate_yaml()
            if e.name == 'wotlk':
                cls['WotLK'] = e.generate_yaml()

        return cls

def create_driver():
    print("Initializing driver...")
    options = FirefoxOptions()
    # options.headless = True
    service = FirefoxService(executable_path=GeckoDriverManager("v0.31.0").install())
    driver = Firefox(service=service, options=options)
    driver.install_addon("extensions/ublock_origin-1.43.0.xpi", temporary=True)
    driver.maximize_window()
    return driver

def close_popups(driver):
    print("Denying cookies...")

    driver.get("https://classic.wowhead.com")

    # ActionChains(driver, 20).scroll_by_amount(0, -1000).pause(0.5).perform()
    time.sleep(1)

    reject_cookies_id = "onetrust-reject-all-handler"
    WebDriverWait(driver, 10).until(
        ec.element_to_be_clickable((By.ID, reject_cookies_id)))
    reject_cookies = driver.find_element(By.ID, reject_cookies_id)
    ActionChains(driver, 10).click(reject_cookies).pause(1).perform()

    no_notifications_class = "notifications-dialog-buttons-decline"
    WebDriverWait(driver, 10).until(
        ec.element_to_be_clickable((By.CLASS_NAME, no_notifications_class)))
    no_notifications = driver.find_element(By.CLASS_NAME, no_notifications_class)
    ActionChains(driver, 10).click(no_notifications).pause(1).perform()

    # the video player is blocked with ublock

def scrape(driver):
    close_popups(driver)

    classes_to_scrape = [
        ('druid',        ['classic', 'tbc', 'wotlk']),
        ('hunter',       ['classic', 'tbc', 'wotlk']),
        ('mage',         ['classic', 'tbc', 'wotlk']),
        ('paladin',      ['classic', 'tbc', 'wotlk']),
        ('priest',       ['classic', 'tbc', 'wotlk']),
        ('rogue',        ['classic', 'tbc', 'wotlk']),
        ('shaman',       ['classic', 'tbc', 'wotlk']),
        ('warlock',      ['classic', 'tbc', 'wotlk']),
        ('warrior',      ['classic', 'tbc', 'wotlk']),
        ('death-knight', ['wotlk']),
    ]
    # fun factor
    random.shuffle(classes_to_scrape)

    classes = []
    for class_name, expansions in classes_to_scrape:
        model = ClassModel()
        model.scrape(driver, class_name, expansions)
        classes.append(model)

    return classes

def download_images(classes: List[ClassModel]):
    print("Downloading image files...")
    # remote the output dir if it already exists
    # rmtree('out')
    os.makedirs('out/images', exist_ok=True)
    for class_model in classes:
        class_model.download_images('out/images')

def generate_yaml(classes: List[ClassModel]):
    print("Generating yaml...")
    os.makedirs('out/talents', exist_ok=True)

    yaml.representer.SafeRepresenter.add_representer(Location, represent_location)
    for class_model in classes:
        output_file = f"out/talents/{class_model.name}.yml"
        print(f'{class_model.name} -> "{output_file}"')
        with open(output_file, 'w') as f:
            yaml.safe_dump(
                data=class_model.generate_yaml(),
                stream=f,
                default_flow_style=False,
                sort_keys=False,
            )

def format_time(t):
    t = int(t)
    min = t // 60
    sec = t % 60

    return f"{min} min {sec} sec" if min > 0 else f"{sec} sec"

def main():
    start_time = time.time()
    try:
        driver = None
        try:
            driver = create_driver()
            classes = scrape(driver)
        finally:
            if driver:
                driver.quit()

        download_images(classes)
        generate_yaml(classes)
    except BaseException:
        elapsed_time = time.time() - start_time
        print(f"Failed in {format_time(elapsed_time)} seconds.", file=sys.stderr)
        raise
    else:
        elapsed_time = time.time() - start_time
        print(f"Done in {format_time(elapsed_time)} seconds.")

if __name__ == '__main__':
    main()
