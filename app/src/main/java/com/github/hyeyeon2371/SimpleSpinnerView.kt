package com.github.hyeyeon2371

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.ListPopupWindow
import androidx.databinding.DataBindingUtil
import java.lang.RuntimeException

class SimpleSpinnerView : LinearLayout {
    private lateinit var mBinding: com.github.hyeyeon2371.databinding.LayoutSimplespinnerviewBinding
    private lateinit var mPopup: ListPopupWindow
    private lateinit var mAdapter: ListAdapter
    private var mOnItemClickTask: (position: Int) -> Unit = {}

    // attrs
    private lateinit var mTypedArray: TypedArray
    private var mPlaceHolder = resources.getString(R.string.ssv_default_placeholder)
    private var mMaxVisibleCount = 0
    private var mIsDisabled = false
    private var mItemTextColor = 0
    private var mItemTextSize = 13f

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_simplespinnerview, this, true)
        mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleSpinnerView)

        initPopup()
        initAttrs()
        initPlaceHolder()
        setListeners()

        if (::mTypedArray.isInitialized) mTypedArray.recycle()
        mBinding.executePendingBindings()
    }

    private fun initPopup() {
        mPopup = ListPopupWindow(context, null, 0, R.style.SimpleSpinnerView)
        mPopup.anchorView = mBinding.linearlayoutCustomspinnerContainer
        mPopup.isModal = true
        mPopup.setListSelector(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initPlaceHolder() {
        val placeHolderTextColor = if (::mTypedArray.isInitialized && !mIsDisabled)
            mTypedArray.getColor(
                R.styleable.SimpleSpinnerView_placeHolderColor,
                resources.getColor(R.color.ssvDefaultTextColor)
            )
        else resources.getColor(R.color.ssvDefaultTextColor)

        mBinding.textviewCustomspinnerPlaceholder.setTextColor(placeHolderTextColor)
        mBinding.textviewCustomspinnerPlaceholder.setTextSize(TypedValue.COMPLEX_UNIT_PX, mItemTextSize)
        mBinding.textviewCustomspinnerPlaceholder.text = mPlaceHolder

        if (mIsDisabled)
            mBinding.linearlayoutCustomspinnerContainer.background =
                context.resources.getDrawable(R.drawable.ssv_placeholder_bg_disabled)

    }

    private fun initAttrs() {
        if (::mTypedArray.isInitialized) {
            mPlaceHolder = mTypedArray.getString(R.styleable.SimpleSpinnerView_placeHolder)
                ?: resources.getString(R.string.ssv_default_placeholder)
            mMaxVisibleCount = mTypedArray.getInteger(R.styleable.SimpleSpinnerView_visibleCount, 0)
            mIsDisabled = mTypedArray.getBoolean(R.styleable.SimpleSpinnerView_isDisabled, false)
            mItemTextSize = mTypedArray.getDimensionPixelSize(
                R.styleable.SimpleSpinnerView_textSize,
                resources.getDimensionPixelSize(R.dimen.ssv_default_text_size)
            ).toFloat()
            mItemTextColor =
                mTypedArray.getColor(
                    R.styleable.SimpleSpinnerView_textColor,
                    resources.getColor(R.color.ssvDefaultTextColor)
                )
        }
    }

    private fun setListeners() {
        mBinding.linearlayoutCustomspinnerContainer.setOnClickListener {
            if (!mIsDisabled) {
                if (mPopup.isShowing) collapse()
                else expand()
            }
        }

        mPopup.setOnItemClickListener { parent, view, position, id ->
            if (mAdapter.count > position) {
                val item = mAdapter.getItem(position)
                mBinding.textviewCustomspinnerPlaceholder.text = item.toString()
            }

            mOnItemClickTask(position)
            collapse()
        }

        mPopup.setOnDismissListener {
            mBinding.imageviewCustomspinnerArrow.animate().rotation(0f).start()
        }
    }

    private fun setListHeight(itemCount: Int) {
        if (mMaxVisibleCount > 0 && itemCount > mMaxVisibleCount) {
            val view = mAdapter.getView(0, null, mPopup.listView)
            val ms = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            view.measure(ms, ms)
            val itemHeight = view.measuredHeight
            val height = itemHeight * mMaxVisibleCount
            mPopup.height = height + (mPopup.listView?.dividerHeight ?: 0 * mMaxVisibleCount)
        }
    }

    fun <T> setItems(list: MutableList<T>) {
        mAdapter = CustomSpinnerListAdapter(list)
        mPopup.setAdapter(mAdapter)
        setListHeight(list.size)
    }

    fun setAdapter(adapter: ListAdapter) {
        mAdapter = adapter
        mPopup.setAdapter(adapter)
        setListHeight(adapter.count)
    }

    fun setOnItemClickTask(task: (position: Int) -> Unit) {
        this.mOnItemClickTask = task
    }

    fun setPlaceHolder(placeHolder: String) {
        this.mPlaceHolder = placeHolder
        mBinding.textviewCustomspinnerPlaceholder.text = mPlaceHolder
    }

    fun expand() {
        try {
            mPopup.show()
            mBinding.imageviewCustomspinnerArrow.animate().rotation(180f).start()
            mPopup.listView?.overScrollMode = View.OVER_SCROLL_NEVER
            mPopup.listView?.isVerticalScrollBarEnabled = false
        } catch (e: RuntimeException) {
            if (mPopup.isShowing)
                mPopup.dismiss()
        }
    }

    fun collapse() {
        if(mPopup.isShowing){
            mBinding.imageviewCustomspinnerArrow.animate().rotation(0f).start()
            mPopup.dismiss()
        }
    }

    private fun convertDpToPx(dp: Int) =
        (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

    inner class CustomSpinnerListAdapter<T>(val list: MutableList<T>) : BaseAdapter() {
        private lateinit var mBinding: com.github.hyeyeon2371.databinding.ItemSimplespinnerviewBinding

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            mBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_simplespinnerview, parent, false)
            mBinding.textviewCustomspinnerList.text = list[position].toString()
            mBinding.textviewCustomspinnerList.setTextColor(mItemTextColor)
            mBinding.textviewCustomspinnerList.setTextSize(TypedValue.COMPLEX_UNIT_PX, mItemTextSize)
            return mBinding.root
        }

        override fun getItem(position: Int): Any = list[position].toString()
        override fun getItemId(position: Int): Long = 0
        override fun getCount(): Int = list.size
        override fun isEnabled(p0: Int): Boolean = list.isNotEmpty()
        override fun areAllItemsEnabled(): Boolean = !list.isNullOrEmpty()
        override fun hasStableIds(): Boolean = false
        override fun unregisterDataSetObserver(p0: DataSetObserver?) {}
        override fun getViewTypeCount(): Int = 1
        override fun getItemViewType(p0: Int): Int = 1
        override fun registerDataSetObserver(p0: DataSetObserver?) {}
        override fun isEmpty(): Boolean = list.isEmpty()
    }

}