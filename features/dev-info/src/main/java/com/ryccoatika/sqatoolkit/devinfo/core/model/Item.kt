package com.ryccoatika.sqatoolkit.devinfo.core.model

import java.time.Instant

internal sealed interface Item

internal data class DeviceCardItem(
  val androidName: String,
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

internal data class ElapsedTimeItem(
  val label: Label,
  val value: Instant?,
) : Item

internal data class RawTextItem(
  val label: String,
  val value: String,
) : Item

internal data class PermissionItem(
  val label: String,
  val permission: String,
  val value: String,
) : Item

internal data class ExpandableGroupItem(
  val title: String,
  val summary: String,
  val items: List<Item>,
) : Item

internal data class GroupItem(
  val label: Label? = null,
  val rawTitle: String? = null,
  val items: List<Item>,
) : Item
