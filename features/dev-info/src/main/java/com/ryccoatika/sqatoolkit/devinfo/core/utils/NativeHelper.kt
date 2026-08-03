package com.ryccoatika.sqatoolkit.devinfo.core.utils

internal object NativeHelper {
  init {
    System.loadLibrary("dev-info")
  }

  external fun getProp(key: String): String

  external fun execute(cmd: String): String
}
