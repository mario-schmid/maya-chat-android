/*
 * Copyright (c) 2025 Element Creations Ltd.
 * Copyright 2024, 2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-Element-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.element.android.features.home.impl.datasource

import dev.zacsweers.metro.Inject
import io.element.android.features.home.impl.model.LatestEvent
import io.element.android.features.home.impl.model.RoomListRoomSummary
import io.element.android.features.home.impl.model.RoomSummaryDisplayType
import io.element.android.libraries.core.extensions.orEmpty
import io.element.android.libraries.dateformatter.api.DateFormatter
import io.element.android.libraries.dateformatter.api.DateFormatterMode
import io.element.android.libraries.dateformatter.api.MayaCalendarHelper
import io.element.android.libraries.designsystem.components.avatar.AvatarSize
import io.element.android.libraries.eventformatter.api.RoomLatestEventFormatter
import io.element.android.libraries.matrix.api.room.CallIntentConsensus
import io.element.android.libraries.matrix.api.room.CurrentUserMembership
import io.element.android.libraries.matrix.api.room.RoomInfo
import io.element.android.libraries.matrix.api.roomlist.LatestEventValue
import io.element.android.libraries.matrix.api.roomlist.RoomSummary
import io.element.android.libraries.matrix.ui.model.dmUserStatus
import io.element.android.libraries.matrix.ui.model.getAvatarData
import io.element.android.libraries.matrix.ui.model.toInviteSender
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.collections.immutable.toImmutableList

@Inject
class RoomListRoomSummaryFactory(
    private val dateFormatter: DateFormatter,
    private val roomLatestEventFormatter: RoomLatestEventFormatter,
) {
    fun create(roomSummary: RoomSummary): RoomListRoomSummary {
        val roomInfo = roomSummary.info
        val avatarData = roomInfo.getAvatarData(size = AvatarSize.RoomListItem)
        return RoomListRoomSummary(
            id = roomSummary.roomId.value,
            roomId = roomSummary.roomId,
            name = roomInfo.name,
            numberOfUnreadMessages = roomInfo.numUnreadMessages,
            numberOfUnreadMentions = roomInfo.numUnreadMentions,
            numberOfUnreadNotifications = roomInfo.numUnreadNotifications,
            isMarkedUnread = roomInfo.isMarkedUnread,
            timestamp = let {
                val latestEventTimestamp = roomSummary.latestEventTimestamp
                val baseTimestamp = dateFormatter.format(
                    timestamp = latestEventTimestamp,
                    mode = DateFormatterMode.TimeOrDate,
                    useRelative = true,
                )
                if (latestEventTimestamp != null && baseTimestamp.isNotEmpty()) {
                    val isToday = Instant.ofEpochMilli(latestEventTimestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate() == LocalDate.now()
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = Color(0xFF5FB336))) {
                            if (isToday) {
                                append(baseTimestamp)
                            } else {
                                val mayaDate = MayaCalendarHelper.getMayaDate(latestEventTimestamp)
                                append("${mayaDate.day} ${mayaDate.winalName}")
                            })
                        }
                    }
                } else {
                    baseTimestamp
                }
            },
            latestEvent = computeLatestEvent(roomSummary.latestEvent, roomInfo.isDm),
            avatarData = avatarData,
            userDefinedNotificationMode = roomInfo.userDefinedNotificationMode,
            hasRoomCall = roomInfo.hasRoomCall,
            activeCallIntent = when (val consensus = roomInfo.activeCallIntentConsensus) {
                is CallIntentConsensus.Full -> consensus.callIntent
                is CallIntentConsensus.Partial -> consensus.callIntent
                CallIntentConsensus.None -> null
            },
            isDirect = roomInfo.isDirect,
            isFavorite = roomInfo.isFavorite,
            inviteSender = roomInfo.inviter?.toInviteSender(),
            isDm = roomInfo.isDm,
            canonicalAlias = roomInfo.canonicalAlias,
            displayType = when (roomInfo.currentUserMembership) {
                CurrentUserMembership.INVITED -> {
                    RoomSummaryDisplayType.INVITE
                }
                CurrentUserMembership.KNOCKED -> {
                    RoomSummaryDisplayType.KNOCKED
                }
                else -> {
                    RoomSummaryDisplayType.ROOM
                }
            },
            heroes = roomInfo.heroes.map { user ->
                user.getAvatarData(size = AvatarSize.RoomListItem)
            }.toImmutableList(),
            isTombstoned = roomInfo.successorRoom != null,
            isSpace = roomInfo.isSpace,
            dmUserStatus = roomInfo.dmUserStatus(),
        )
    }

    private fun RoomInfo.hasOnlyTwoMembers(): Boolean {
        return isDm || activeMembersCount <= 2
    }

    private fun computeLatestEvent(latestEvent: LatestEventValue, dm: Boolean): LatestEvent {
        return when (latestEvent) {
            is LatestEventValue.None -> {
                LatestEvent.None
            }
            is LatestEventValue.Local -> {
                if (latestEvent.isSending) {
                    val content = roomLatestEventFormatter.format(latestEvent, dm).orEmpty()
                    LatestEvent.Sending(
                        content = content,
                    )
                } else {
                    LatestEvent.Error
                }
            }
            is LatestEventValue.Remote -> {
                val content = roomLatestEventFormatter.format(latestEvent, dm).orEmpty()
                LatestEvent.Synced(
                    content = content,
                )
            }
            is LatestEventValue.RoomInvite -> LatestEvent.None
        }
    }
}
