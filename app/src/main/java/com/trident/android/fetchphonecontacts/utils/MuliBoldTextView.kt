package com.trident.android.fetchphonecontacts.utils

import android.content.Context
import android.util.AttributeSet
import com.trident.android.fetchphonecontacts.utils.FontCache.getTypeface

/**
 * @author SURYA DEVI
 */
class MuliBoldTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {}

    private fun applyCustomFont(font: Context) {
        val customFont = getTypeface("Muli-Bold.ttf", font)
        setTypeface(customFont)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        applyCustomFont(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyCustomFont(context)
    }
}
