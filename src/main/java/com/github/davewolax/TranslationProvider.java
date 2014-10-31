package com.github.davewolax;

import lombok.NonNull;

public interface TranslationProvider {

	public String getTranslationOf(@NonNull final String string, @NonNull final String fromLang, @NonNull final String toLang);
}
