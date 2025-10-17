package com.ryccoatika.sqatools.devinfo.core.model

import java.time.Instant

internal sealed interface Item

internal data class DeviceCardItem(
  val androidVersion: String,
  val internalCodename: String,
  val sdkVersion: Int,
  val releaseDate: Instant?,
) : Item

internal data class TextItem(
  val label: Label,
  val value: String,
) : Item

internal data class StatusItem(
  val label: Label,
  val value: Boolean,
) : Item

internal data class DateItem(
  val label: Label,
  val value: Instant?,
  val isPeriod: Boolean = false,
) : Item

internal data class GroupItem(
  val label: Label?,
  val items: List<Item>,
) : Item
