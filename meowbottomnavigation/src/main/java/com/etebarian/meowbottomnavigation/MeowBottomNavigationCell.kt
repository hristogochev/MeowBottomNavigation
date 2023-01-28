package com.etebarian.meowbottomnavigation

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.etebarian.meowbottomnavigation.databinding.MeowNavigationCellBinding

/**
 * Created by 1HE on 2/23/2019.
 */

@Suppress("unused")
class MeowBottomNavigationCell : RelativeLayout {

    private lateinit var binding: MeowNavigationCellBinding

    companion object {
        const val EMPTY_VALUE = "empty"
    }

    var defaultIconColor = 0
        set(value) {
            field = value
            if (allowDraw)
                binding.iv.color = if (!isEnabledCell) defaultIconColor else selectedIconColor
        }
    var selectedIconColor = 0
        set(value) {
            field = value
            if (allowDraw)
                binding.iv.color = if (isEnabledCell) selectedIconColor else defaultIconColor
        }
    var circleColor = 0
        set(value) {
            field = value
            if (allowDraw)
                isEnabledCell = isEnabledCell
        }

    var icon = 0
        set(value) {
            field = value
            if (allowDraw)
                binding.iv.resource = value
        }

    var count: String? = EMPTY_VALUE
        set(value) {
            field = value
            if (allowDraw) {
                if (count != null && count == EMPTY_VALUE) {
                    binding.tvCount.text = ""
                    binding.tvCount.visibility = View.INVISIBLE
                } else {
                    if (count != null && (count?.length ?: 0) >= 3) {
                        field = count?.substring(0, 1) + ".."
                    }
                    binding.tvCount.text = count
                    binding.tvCount.visibility = View.VISIBLE
                    val scale = if (count?.isEmpty() == true) 0.5f else 1f
                    binding.tvCount.scaleX = scale
                    binding.tvCount.scaleY = scale
                }
            }
        }

    private var iconSize = dip(context, 48)
        set(value) {
            field = value
            if (allowDraw) {
                binding.iv.size = value
                binding.iv.pivotX = iconSize / 2f
                binding.iv.pivotY = iconSize / 2f
            }
        }

    var countTextColor = 0
        set(value) {
            field = value
            if (allowDraw)
                binding.tvCount.setTextColor(field)
        }

    var countBackgroundColor = 0
        set(value) {
            field = value
            if (allowDraw) {
                val d = GradientDrawable()
                d.setColor(field)
                d.shape = GradientDrawable.OVAL
                ViewCompat.setBackground(binding.tvCount, d)
            }
        }

    var countTypeface: Typeface? = null
        set(value) {
            field = value
            if (allowDraw && field != null)
                binding.tvCount.typeface = field
        }

    var rippleColor = 0
        set(value) {
            field = value
            if (allowDraw) {
                isEnabledCell = isEnabledCell
            }
        }

    var isFromLeft = false
    var duration = 0L
    private var progress = 0f
        set(value) {
            field = value
            binding.fl.y = (1f - progress) * dip(context, 18) + dip(context, -2)

            binding.iv.color = if (progress == 1f) selectedIconColor else defaultIconColor
            val scale = (1f - progress) * (-0.2f) + 1f
            binding.iv.scaleX = scale
            binding.iv.scaleY = scale

            val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL

            ViewCompat.setBackground(binding.vCircle, d)

            ViewCompat.setElevation(
                binding.vCircle,
                if (progress > 0.7f) dipf(context, progress * 4f) else 0f
            )

            val m = dip(context, 24)
            binding.vCircle.x =
                (1f - progress) * (if (isFromLeft) -m else m) + ((measuredWidth - dip(
                    context,
                    48
                )) / 2f)
            binding.vCircle.y = (1f - progress) * measuredHeight + dip(context, 6)
        }

    var isEnabledCell = false
        set(value) {
            field = value
            val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL
            if (!isEnabledCell) {
                binding.fl.background = RippleDrawable(ColorStateList.valueOf(rippleColor), null, d)
            } else {
                binding.fl.runAfterDelay(200) {
                    binding.fl.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

    var onClickListener: () -> Unit = {}
        set(value) {
            field = value
            binding.iv.setOnClickListener {
                onClickListener()
            }
        }

    private var allowDraw = false

    constructor(context: Context) : super(context) {
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
    }

    private fun initializeView() {
        allowDraw = true
        binding = MeowNavigationCellBinding.inflate(LayoutInflater.from(context),this)
        draw()
    }

    private fun draw() {
        if (!allowDraw)
            return

        icon = icon
        count = count
        iconSize = iconSize
        countTextColor = countTextColor
        countBackgroundColor = countBackgroundColor
        countTypeface = countTypeface
        rippleColor = rippleColor
        onClickListener = onClickListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        progress = progress
    }

    fun disableCell() {
        if (isEnabledCell)
            animateProgress(false)
        isEnabledCell = false
    }

    fun enableCell(isAnimate: Boolean = true) {
        if (!isEnabledCell)
            animateProgress(true, isAnimate)
        isEnabledCell = true
    }

    private fun animateProgress(enableCell: Boolean, isAnimate: Boolean = true) {
        val d = if (enableCell) duration else 250
        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.apply {
            startDelay = if (enableCell) d / 4 else 0L
            duration = if (isAnimate) d else 1L
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                val f = it.animatedFraction
                progress = if (enableCell)
                    f
                else
                    1f - f
            }
            start()
        }
    }
}