package com.apatar.functions.String;

import java.util.List;
import java.util.regex.Matcher;

import com.apatar.core.AbstractApatarFunction;
import com.apatar.core.ApatarRegExp;
import com.apatar.functions.FunctionInfo;
import com.apatar.ui.FunctionCategory;

public class ParseGoldMineNotesFunction extends AbstractApatarFunction {

	@Override
	public Object execute(List l) {
		Object data = l.get(0);
		if (data instanceof String) {
			try {
				String allNotes = (String) data;
				int notesCount = 0;
				// getting notes count
				List<String> result = ApatarRegExp.getAllSubstrings(
						"(?m)(?s)(\\*{3}.*?\\*{3})", allNotes, 1);
				notesCount = result.size();
				String res = allNotes;
				for (int i = 0; i < notesCount; i++) {
					if ((i + 1) == notesCount) {
						if (isNoteDeleted(allNotes)) {
							res = res
									.replaceFirst(
											"(?m)(?s)(.*)\\*{3}.*?\\*{3}.*?~~deleted=1.*",
											"$1");
						}
					} else {
						if (isNoteDeleted(getNote(allNotes))) {
							res = res
									.replaceFirst(
											"(?m)(?s)(.*)\\*{3}.*?\\*{3}.*?~~deleted=1.*?(\\*{3}.*)",
											"$1$2");
						}
					}
					allNotes = allNotes.replaceFirst(
							"(?m)(?s)\\*{3}.*?\\*{3}.*?(\\*{3})", "$1");
				}
				return res;
			} catch (Exception e) {
				e.printStackTrace();
				return data;
			}
		}
		return data;
	}

	private boolean isNoteDeleted(String note) throws Exception {
		Matcher res = ApatarRegExp.getMatcher(
				"(?m)(?s)\\*{3}.*?\\*{3}.*?~~deleted=1.*", note);
		return res.matches();
	}

	private String getNote(String notes) throws Exception {
		return ApatarRegExp.getSubstrings(
				"(?m)(?s)(\\*{3}.*?\\*{3}.*?)\\*{3}.*", notes, 1);
	}

	static FunctionInfo fi = new FunctionInfo("Parse GoldMine Notes", 1, 1);
	static {
		fi.getCategories().add(FunctionCategory.String);
		fi.getCategories().add(FunctionCategory.ALL);
	}

	public FunctionInfo getFunctionInfo() {
		return fi;
	}

}
