package com.loohp.bookshelf.api;

import com.loohp.bookshelf.Bookshelf;

public class BookshelfAPI {
	
	/**
	 * Get the size of a default bookshelf
	 * @return the amount of slots
	 */
	public static int getBookshelfSize() {
		return Bookshelf.bookShelfRows * 9;
	}
	
}
