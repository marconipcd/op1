package com.digital.opuserp.test;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.TextField;

public class teste  extends TextField{

	private Map<String, String> searchPairs = new LinkedHashMap<String, String>();

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		String[] s = searchPairs.keySet().toArray(
				new String[searchPairs.size()]);
		String[] r = new String[s.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = searchPairs.get(s[i]);
		}
		target.addAttribute("SEARCH", s);
		target.addAttribute("REPLACE", r);
	}

	/**
	 * Given rules will be applied order with
	 * {@link String#replaceAll(String, String)} to the field value after each
	 * key press on the client side.
	 * 
	 * @param search
	 * @param replace
	 */
	public void addReplaceRule(String search, String replace) {
		searchPairs.put(search, replace);
	}

	/**
	 * Applies auto replace rules for given String on the server side.
	 * 
	 * @param string
	 * @return a string with all rules applied
	 */
	protected String applyRules(String string) {
		if (string != null && searchPairs != null) {
			for (String key : searchPairs.keySet()) {
				string = string.replaceAll(key, searchPairs.get(key));
			}
			return string;
		}
		return string;
	}
}
