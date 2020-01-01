package com.iammert.tabscrollattacherlib

internal sealed class ScrollMethod {
    object Direct : ScrollMethod()
    object Smooth : ScrollMethod()
    class LimitedSmooth(val limit: Int) : ScrollMethod()
}