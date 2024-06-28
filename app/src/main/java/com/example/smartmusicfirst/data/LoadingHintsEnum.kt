package com.example.smartmusicfirst.data

import androidx.annotation.StringRes
import com.example.smartmusicfirst.R

enum class LoadingHintsEnum(@StringRes val hintState: Int) {
    START(hintState = R.string.start_search_method),
    KEYWORD_EXTRACT(hintState = R.string.keyword_extract),
    GET_AI_OFFER(hintState = R.string.get_ai_offer),
    SONGS_EXTRACT(hintState = R.string.songs_extract),
    BUILD_PLAYLIST(hintState = R.string.build_playlist)
}