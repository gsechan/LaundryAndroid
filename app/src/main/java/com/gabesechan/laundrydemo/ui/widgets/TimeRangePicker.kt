package com.gabesechan.laundrydemo.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabesechan.laundrydemo.R
import com.gabesechan.laundrydemo.laundromatinfo.TimeRange
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.collections.forEach

@Composable
fun DisplayTimes(times: List<TimeRange>, selected: TimeRange?, onTimeSelected: (TimeRange)->Unit) {
    val dateFormat = SimpleDateFormat("hh:mma")
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val unselectedModifier = Modifier.width(120.dp).height(32.dp).background(MaterialTheme.colorScheme.primary)
        val selectedModifier = Modifier.width(120.dp).height(32.dp).border(width = 2.dp, color = Color.Black)
            .background(MaterialTheme.colorScheme.secondaryContainer)
        times.forEach{ time->
            val startTime = dateFormat.format(time.startTime)
            val endTime = dateFormat.format(time.endTime)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = (if(selected == time) selectedModifier else unselectedModifier).clickable { onTimeSelected(time) }
            ) {
                Text(
                    text= stringResource(R.string.time_range_format, startTime, endTime),
                    color = if(selected==time) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary,
                    fontSize = 8.sp,
                )
            }
        }

    }
}


