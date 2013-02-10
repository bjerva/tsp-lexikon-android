package com.bjerva.tsplex;

import java.util.List;

import android.content.Context;
import android.widget.SectionIndexer;

class AlphabeticSectionAdapter extends SignAdapter implements SectionIndexer {
	
	private String[] mSections = new String[] {"A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
			"Y", "Z", "Å", "Ä", "Ö", "0", "1"};
			 
	
	public AlphabeticSectionAdapter(Context context, int textViewResourceId,
			List<GsonSign> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section will be selected
		char currChar;

		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				currChar = getItem(j).words.get(0).word.charAt(0);
				if (currChar == mSections[i].charAt(0)){
					return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length];
		for (int i = 0; i < mSections.length; i++)
			sections[i] = String.valueOf(mSections[i]);
		return sections;
	}
}
