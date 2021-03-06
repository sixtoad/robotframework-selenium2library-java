package com.github.markusbernhardt.selenium2library.keywords;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.Select;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.Autowired;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;
import org.robotframework.javalib.annotation.RobotKeywords;

import com.github.markusbernhardt.selenium2library.RunOnFailureKeywordsAdapter;
import com.github.markusbernhardt.selenium2library.Selenium2LibraryNonFatalException;
import com.github.markusbernhardt.selenium2library.locators.ElementFinder;
import com.github.markusbernhardt.selenium2library.utils.Python;

@RobotKeywords
public class SelectElement extends RunOnFailureKeywordsAdapter {

	/**
	 * Instantiated BrowserManagement keyword bean
	 */
	@Autowired
	protected BrowserManagement browserManagement;

	/**
	 * Instantiated Element keyword bean
	 */
	@Autowired
	protected Element element;

	/**
	 * Instantiated Logging keyword bean
	 */
	@Autowired
	protected Logging logging;

	// ##############################
	// Keywords
	// ##############################

	/**
	 * Returns the values in the select list identified by <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @return The select list values
	 */
	@RobotKeyword
	@ArgumentNames({ "locator" })
	public List<String> getListItems(String locator) {
		List<WebElement> options = getSelectListOptions(locator);

		return getLabelsForOptions(options);
	}

	/**
	 * Returns the visible label of the first selected element from the select
	 * list identified by <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @return The first visible select list label
	 */
	@RobotKeyword
	@ArgumentNames({ "locator" })
	public String getSelectedListLabel(String locator) {
		Select select = getSelectList(locator);

		return select.getFirstSelectedOption().getText();
	}

	/**
	 * Returns the visible labels of the first selected elements as a list from
	 * the select list identified by <b>locator</b>.<br>
	 * <br>
	 * Fails if there is no selection.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @return The list of visible select list labels
	 */
	@RobotKeyword
	@ArgumentNames({ "locator" })
	public List<String> getSelectedListLabels(String locator) {
		List<WebElement> options = getSelectListOptionsSelected(locator);

		if (options.size() == 0) {
			throw new Selenium2LibraryNonFatalException(
					String.format("Select list with locator '%s' does not have any selected values.", locator));
		}

		return getLabelsForOptions(options);
	}

	/**
	 * Returns the value of the first selected element from the select list
	 * identified by <b>locator</b>.<br>
	 * <br>
	 * The return value is read from the value attribute of the selected
	 * element.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @return The first select list value
	 */
	@RobotKeyword
	@ArgumentNames({ "locator" })
	public String getSelectedListValue(String locator) {
		Select select = getSelectList(locator);

		return select.getFirstSelectedOption().getAttribute("value");
	}

	/**
	 * Returns the values of the first selected elements as a list from the
	 * select list identified by <b>locator</b>.<br>
	 * <br>
	 * Fails if there is no selection. The return values are read from the value
	 * attribute of the selected element.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @return The list of select list values
	 */
	@RobotKeyword
	@ArgumentNames({ "locator" })
	public List<String> getSelectedListValues(String locator) {
		List<WebElement> options = getSelectListOptionsSelected(locator);

		if (options.size() == 0) {
			throw new Selenium2LibraryNonFatalException(
					String.format("Select list with locator '%s' does not have any selected values.", locator));
		}

		return getValuesForOptions(options);
	}

	/**
	 * Verify the selection of the select list identified by <b>locator</b>is
	 * exactly <b>*items</b>.<br>
	 * <br>
	 * If you want to verify no option is selected, simply give no items.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @param items
	 *            The list of items to verify
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*items" })
	public void listSelectionShouldBe(String locator, String... items) {
		String itemList = items.length != 0 ? String.format("option(s) [ %s ]", Python.join(" | ", items))
				: "no options";
		logging.info(String.format("Verifying list '%s' has %s selected.", locator, itemList));

		pageShouldContainList(locator);

		List<WebElement> options = getSelectListOptionsSelected(locator);
		List<String> selectedLabels = getLabelsForOptions(options);
		String message = String.format("List '%s' should have had selection [ %s ] but it was [ %s ].", locator,
				Python.join(" | ", items), Python.join(" | ", selectedLabels));
		if (items.length != options.size()) {
			throw new Selenium2LibraryNonFatalException(message);
		} else {
			List<String> selectedValues = getValuesForOptions(options);

			for (String item : items) {
				if (!selectedValues.contains(item) && !selectedLabels.contains(item)) {
					throw new Selenium2LibraryNonFatalException(message);
				}
			}
		}
	}

	/**
	 * Verify the select list identified by <b>locator</b>has no selections.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 */
	@RobotKeyword
	@ArgumentNames({ "locator" })
	public void listShouldHaveNoSelections(String locator) {
		logging.info(String.format("Verifying list '%s' has no selection.", locator));

		List<WebElement> options = getSelectListOptionsSelected(locator);
		if (!options.equals(null)) {
			List<String> selectedLabels = getLabelsForOptions(options);
			String items = Python.join(" | ", selectedLabels);
			throw new Selenium2LibraryNonFatalException(String.format(
					"List '%s' should have had no selection (selection was [ %s ]).", locator, items.toString()));
		}
	}

	@RobotKeywordOverload
	public void pageShouldContainList(String locator) {
		pageShouldContainList(locator, "");
	}

	@RobotKeywordOverload
	public void pageShouldContainList(String locator, String message) {
		pageShouldContainList(locator, message, "INFO");
	}

	/**
	 * Verify the select list identified by <b>locator</b> is found on the
	 * current page.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators and log levels.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @param message
	 *            Default=NONE. Optional custom error message.
	 * @param logLevel
	 *            Default=INFO. Optional log level.
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "message=NONE", "logLevel=INFO" })
	public void pageShouldContainList(String locator, String message, String logLevel) {
		element.pageShouldContainElement(locator, "list", message, logLevel);
	}

	@RobotKeywordOverload
	public void pageShouldNotContainList(String locator) {
		pageShouldNotContainList(locator, "");
	}

	@RobotKeywordOverload
	public void pageShouldNotContainList(String locator, String message) {
		pageShouldNotContainList(locator, message, "INFO");
	}

	/**
	 * Verify the select list identified by <b>locator</b> is not found on the
	 * current page.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators and log levels.<br>
	 * 
	 * @param locator
	 *            The locator to locate the select list.
	 * @param message
	 *            Default=NONE. Optional custom error message.
	 * @param logLevel
	 *            Default=INFO. Optional log level.
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "message=NONE", "logLevel=INFO" })
	public void pageShouldNotContainList(String locator, String message, String logLevel) {
		element.pageShouldNotContainElement(locator, "list", message, logLevel);
	}

	/**
	 * Select all values of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 */
	@RobotKeyword
	@ArgumentNames({ "locator" })
	public void selectAllFromList(String locator) {
		logging.info(String.format("Selecting all options from list '%s'.", locator));

		Select select = getSelectList(locator);
		if (!isMultiselectList(select)) {
			throw new Selenium2LibraryNonFatalException(
					"Keyword 'Select all from list' works only for multiselect lists.");
		}

		for (int i = 0; i < select.getOptions().size(); i++) {
			select.selectByIndex(i);
		}
	}

	/**
	 * Select the given <b>*items</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * An exception is raised for a single-selection list if the last value does
	 * not exist in the list and a warning for all other non-existing items. For
	 * a multi-selection list, an exception is raised for any and all
	 * non-existing values.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param items
	 *            The list of items to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*items" })
	public void selectFromList(String locator, String... items) {
		String itemList = items.length != 0 ? String.format("option(s) [ %s ]", Python.join(" | ", items))
				: "all options";
		logging.info(String.format("Selecting %s from list '%s'.", itemList, locator));

		Select select = getSelectList(locator);
		logging.debug(String.format("Selecting %s in '%s'.", itemList, ArrayUtils.toString(select.getOptions())));
		// If no items given, select all values (of in case of single select
		// list, go through all values)

		if (items.length == 0) {
			for (int i = 0; i < select.getOptions().size(); i++) {
				select.selectByIndex(i);
			}
			return;
		}

		boolean lastItemFound = false;
		List<String> nonExistingItems = new ArrayList<String>();
		for (String item : items) {
			logging.debug(String.format("Iterating %s in '%s'.", ArrayUtils.toString(items), item));
			lastItemFound = true;
			try {
				if (browserManagement.getRemoteCapabilities().contains("marionette=true")){
					this.selectByValue(item, select);
				} else {
					select.selectByValue(item);
				}
				logging.debug("Found by Value");
			} catch (NoSuchElementException e1) {
				try {
					if (browserManagement.getRemoteCapabilities().contains("marionette=true")){
						this.selectByVisibleText(item, select);
					}else {
						select.selectByVisibleText(item);
					}
					logging.debug("Found by Visible Text");
				} catch (NoSuchElementException e2) {
					logging.debug("Not Found");
					nonExistingItems.add(item);
					lastItemFound = false;
					continue;
				}
			}
		}

		if (nonExistingItems.size() != 0) {
			// multi-selection list => throw immediately
			if (select.isMultiple()) {
				throw new Selenium2LibraryNonFatalException(
						String.format("Options '%s' not in list '%s'.", Python.join(", ", nonExistingItems), locator));
			}

			// single-selection list => log warning with not found items
			logging.warn(String.format("Option%s '%s' not found within list '%s'.",
					nonExistingItems.size() == 0 ? "" : "s", Python.join(", ", nonExistingItems), locator));

			// single-selection list => throw if last item was not found
			if (!lastItemFound) {
				throw new Selenium2LibraryNonFatalException(String.format("Option '%s' not in list '%s'.",
						nonExistingItems.get(nonExistingItems.size() - 1), locator));
			}
		}
	}

	/**
	 * Select all options that have a value matching the argument. That is, when
	 * given "foo" this would select an option like:
	 *
	 * &lt;option value="foo"&gt;Bar&lt;/option&gt;
	 *
	 * @param value
	 *            The value to match against
	 * @throws NoSuchElementException
	 *             If no matching option elements are found
	 */
	private void selectByValue(String value, Select select) {
		List<WebElement> options = browserManagement.getCurrentWebDriver().findElements(By.xpath(
		        ".//option[@value = " + Quotes.escape(value) + "]"));

		boolean matched = false;
		for (WebElement option : options) {
			setSelected(option, true);
			if (!select.isMultiple()) {
				return;
			}
			matched = true;
		}

		if (!matched) {
			throw new NoSuchElementException("Cannot locate option with value: " + value);
		}
	}

	private void selectByVisibleText(String text, Select select) {
		// try to find the option via XPATH ...
		List<WebElement> options = browserManagement.getCurrentWebDriver().findElements(By.xpath(".//option[normalize-space(.) = " + Quotes.escape(text) + "]"));

		boolean matched = false;
		for (WebElement option : options) {
			setSelected(option,true);
			if (!select.isMultiple()) {
				return;
			}
			matched = true;
		}

		if (options.isEmpty() && text.contains(" ")) {
			String subStringWithoutSpace = getLongestSubstringWithoutSpace(text);
			List<WebElement> candidates;
			if ("".equals(subStringWithoutSpace)) {
				// hmm, text is either empty or contains only spaces - get all
				// options ...
				candidates = browserManagement.getCurrentWebDriver().findElements(By.tagName("option"));
			} else {
				// get candidates via XPATH ...
				candidates =  browserManagement.getCurrentWebDriver().findElements(By.xpath(".//option[contains(., " +
		                Quotes.escape(subStringWithoutSpace) + ")]"));
			}
			for (WebElement option : candidates) {
				if (text.equals(option.getText())) {
					setSelected(option,true);
					if (!select.isMultiple()) {
						return;
					}
					matched = true;
				}
			}
		}

		if (!matched) {
			throw new NoSuchElementException("Cannot locate element with text: " + text);
		}
	}

	private String getLongestSubstringWithoutSpace(String s) {
		String result = "";
		StringTokenizer st = new StringTokenizer(s, " ");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			if (t.length() > result.length()) {
				result = t;
			}
		}
		return result;
	}

	private void setSelected(WebElement element, boolean selected) {
		this.setAttribute(element, "selected", new Boolean(selected).toString());
	}

	private void setAttribute(WebElement element, String attName, String attValue) {
		((JavascriptExecutor) browserManagement.getCurrentWebDriver())
				.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", element, attName, attValue);
		triggerOnChange(element);
	}
	
	private void triggerOnChange(WebElement element) {
		try {
		((JavascriptExecutor) browserManagement.getCurrentWebDriver())
				.executeScript("arguments[0].parentNode.onchange();", element);
		} catch (Exception e) {
			// No defined onchange, we ignore it.
		}
	}

	/**
	 * Select the given <b>*indexes</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * Tries to select by value AND by label. It's generally faster to use 'by
	 * index/value/label' keywords.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param indexes
	 *            The list of indexes to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*indexes" })
	public void selectFromListByIndex(String locator, String... indexes) {
		if (indexes.length == 0) {
			throw new Selenium2LibraryNonFatalException("No index given.");
		}

		List<String> tmp = new ArrayList<String>();
		for (String index : indexes) {
			tmp.add(index);
		}
		String items = String.format("index(es) '%s'", Python.join(", ", tmp));
		logging.info(String.format("Selecting %s from list '%s'.", items, locator));

		Select select = getSelectList(locator);
		for (String index : indexes) {
			select.selectByIndex(Integer.parseInt(index));
		}
	}

	/**
	 * Select the given <b>*values</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param values
	 *            The list of values to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*values" })
	public void selectFromListByValue(String locator, String... values) {
		if (values.length == 0) {
			throw new Selenium2LibraryNonFatalException("No value given.");
		}

		String items = String.format("value(s) '%s'", Python.join(", ", values));
		logging.info(String.format("Selecting %s from list '%s'.", items, locator));

		Select select = getSelectList(locator);
		for (String value : values) {
			if (browserManagement.getRemoteCapabilities().contains("marionette=true")){
				selectByValue(value,select);
			} else{
				select.selectByValue(value);
			}
			
		}
	}

	/**
	 * Select the given <b>*labels</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param labels
	 *            The list of labels to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*labels" })
	public void selectFromListByLabel(String locator, String... labels) {
		if (labels.length == 0) {
			throw new Selenium2LibraryNonFatalException("No value given.");
		}

		String items = String.format("label(s) '%s'", Python.join(", ", labels));
		logging.info(String.format("Selecting %s from list '%s'.", items, locator));

		Select select = getSelectList(locator);
		for (String label : labels) {
			select.selectByVisibleText(label);
		}
	}

	/**
	 * Unselect the given <b>*items</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * As a special case, giving an empty *items list will remove all
	 * selections.<br>
	 * <br>
	 * Tries to unselect by value AND by label. It's generally faster to use 'by
	 * index/value/label' keywords.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param items
	 *            The list of items to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*items" })
	public void unselectFromList(String locator, String... items) {
		String itemList = items.length != 0 ? String.format("option(s) [ %s ]", Python.join(" | ", items))
				: "all options";
		logging.info(String.format("Unselecting %s from list '%s'.", itemList, locator));

		Select select = getSelectList(locator);

		if (!isMultiselectList(select)) {
			throw new Selenium2LibraryNonFatalException(
					"Keyword 'Unselect from list' works only for multiselect lists.");
		}

		if (items.length == 0) {
			select.deselectAll();

			return;
		}

		for (String item : items) {
			select.deselectByValue(item);
			select.deselectByVisibleText(item);
		}
	}

	/**
	 * Unselect the given <b>*indexes</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param indexes
	 *            The list of indexes to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*indexes" })
	public void unselectFromListByIndex(String locator, Integer... indexes) {
		if (indexes.equals(null)) {
			throw new Selenium2LibraryNonFatalException("No index given.");
		}

		List<String> tmp = new ArrayList<String>();
		for (Integer index : indexes) {
			tmp.add(index.toString());
		}
		String items = String.format("index(es) '%s'", Python.join(", ", tmp));
		logging.info(String.format("Unselecting %s from list '%s'.", items, locator));

		Select select = getSelectList(locator);

		if (!isMultiselectList(select)) {
			throw new Selenium2LibraryNonFatalException(
					"Keyword 'Unselect from list' works only for multiselect lists.");
		}

		for (int index : indexes) {
			select.deselectByIndex(index);
		}
	}

	/**
	 * Unselect the given <b>*values</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param values
	 *            The list of values to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*values" })
	public void unselectFromListByValue(String locator, String... values) {
		if (values.equals(null)) {
			throw new Selenium2LibraryNonFatalException("No value given.");
		}

		String items = String.format("value(s) '%s'", Python.join(", ", values));
		logging.info(String.format("Unselecting %s from list '%s'.", items, locator));

		Select select = getSelectList(locator);

		if (!isMultiselectList(select)) {
			throw new Selenium2LibraryNonFatalException(
					"Keyword 'Unselect from list' works only for multiselect lists.");
		}

		for (String value : values) {
			select.deselectByValue(value);
		}
	}

	/**
	 * Unselect the given <b>*labels</b> of the multi-select list identified by
	 * <b>locator</b>.<br>
	 * <br>
	 * Select list keywords work on both lists and combo boxes. Key attributes
	 * for select lists are id and name. See `Introduction` for details about
	 * locators.<br>
	 * 
	 * @param locator
	 *            The locator to locate the multi-select list.
	 * @param labels
	 *            The list of labels to select
	 */
	@RobotKeyword
	@ArgumentNames({ "locator", "*labels" })
	public void unselectFromListByLabel(String locator, String... labels) {
		if (labels.equals(null)) {
			throw new Selenium2LibraryNonFatalException("No value given.");
		}

		String items = String.format("label(s) '%s'", Python.join(", ", labels));
		logging.info(String.format("Unselecting %s from list '%s'.", items, locator));

		Select select = getSelectList(locator);

		if (!isMultiselectList(select)) {
			throw new Selenium2LibraryNonFatalException(
					"Keyword 'Unselect from list' works only for multiselect lists.");
		}

		for (String label : labels) {
			select.deselectByVisibleText(label);
		}
	}

	// ##############################
	// Internal Methods
	// ##############################

	protected List<String> getLabelsForOptions(List<WebElement> options) {
		List<String> labels = new ArrayList<String>();

		for (WebElement option : options) {
			labels.add(option.getText());
		}

		return labels;
	}

	protected Select getSelectList(String locator) {
		List<WebElement> webElements = element.elementFind(locator, true, true, "select");

		return new Select(webElements.get(0));
	}

	protected List<WebElement> getSelectListOptions(Select select) {
		return new ArrayList<WebElement>(select.getOptions());
	}

	protected List<WebElement> getSelectListOptions(String locator) {
		Select select = getSelectList(locator);

		return getSelectListOptions(select);
	}

	protected List<WebElement> getSelectListOptionsSelected(String locator) {
		Select select = getSelectList(locator);

		return new ArrayList<WebElement>(select.getAllSelectedOptions());
	}

	protected List<String> getValuesForOptions(List<WebElement> options) {
		ArrayList<String> labels = new ArrayList<String>();

		for (WebElement option : options) {
			labels.add(option.getAttribute("value"));
		}

		return labels;
	}

	protected boolean isMultiselectList(Select select) {
		return select.isMultiple();
	}

}
