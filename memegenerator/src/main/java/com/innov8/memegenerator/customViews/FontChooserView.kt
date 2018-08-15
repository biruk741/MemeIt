package com.innov8.memegenerator.customViews

import android.content.Context
import android.util.AttributeSet
import com.innov8.memegenerator.models.MyTypeFace
import com.jaredrummler.materialspinner.MaterialSpinner

class FontChooserView : MaterialSpinner {


    lateinit var typefaceLoaders:List<MyTypeFace>
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        //todo customize the item views to show the font
        typefaceLoaders=MyTypeFace.getTypefaceFiles()
        setItems(*typefaceLoaders.map { it.name }.toTypedArray())
    }
    fun getSelectedFont():MyTypeFace =typefaceLoaders[selectedIndex]


}
