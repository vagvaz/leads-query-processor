package com.apatar.functions.String;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class DescFunction extends AbstractApatarFunction {

	public Object execute(List list) {
		/* @@ -->
		// String str = list.get(0).toString();

		String expression = "(.*?)(<html.*?>.*?</html>)(.*)";

		if (list.size() == 1) {
			if ((list.get(0) instanceof String)
					&& (expression instanceof String)) {

				if (list.get(0).toString().length() == 0) {
					return list.get(0);
				}

				String res = "";
				Pattern patt = Pattern.compile("<head.*?>.*?</head>",
						Pattern.MULTILINE + Pattern.CASE_INSENSITIVE
								+ Pattern.DOTALL);
				Matcher match = patt.matcher(list.get(0).toString());
				res = match.replaceAll("");
				// res =
				// list.get(0).toString().replaceAll("<head.*?>.*?</head>", "");
				patt = Pattern.compile("<style.*?>(.*?)</style>",
						Pattern.MULTILINE + Pattern.CASE_INSENSITIVE
								+ Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				patt = Pattern.compile("<[^\\s].*?>", Pattern.MULTILINE
						+ Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				patt = Pattern.compile("&lt;[^\\s].*?&gt;", Pattern.MULTILINE
						+ Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				patt = Pattern.compile("&[a-z]{1,6};", Pattern.MULTILINE
						+ Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
				match = patt.matcher(res);
				res = match.replaceAll("");

				res = res.replaceAll("\\x00", " ");

				return res;

			} else {
				return list.get(0);
			}
		} else {
			return list.get(0);
		}
		<-- @@ */
		return null;
	}

	static FunctionInfo fi = new FunctionInfo("DESC", 1, 1);
	static {
		//fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.Sort);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
