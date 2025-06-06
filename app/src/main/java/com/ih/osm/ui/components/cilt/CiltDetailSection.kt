package com.ih.osm.ui.components.cilt

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ih.osm.R
import com.ih.osm.domain.model.CiltData
import com.ih.osm.ui.components.ExpandableCard
import com.ih.osm.ui.components.SectionTag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun CiltDetailSection(
    data: CiltData,
    navController: NavController,
) {
    data.positions.forEach { position ->
        position.ciltMasters.forEach { cilt ->
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.position_label, position.name),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(R.string.routine_label, cilt.ciltName),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = stringResource(R.string.description_label, cilt.ciltDescription),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            ExpandableCard(title = stringResource(R.string.cilt_details), expanded = true) {
                SectionTag(
                    title = stringResource(R.string.cilt_due_date),
                    value = formatIsoDate(cilt.ciltDueDate),
                    isErrorEnabled = isDateOverdue(cilt.ciltDueDate),
                )
                SectionTag(title = stringResource(R.string.cilt_created_by), value = cilt.creatorName)
                SectionTag(title = stringResource(R.string.cilt_reviewed_by), value = cilt.reviewerName)
                SectionTag(title = stringResource(R.string.cilt_approved_by), value = cilt.approvedByName)
                SectionTag(
                    title = stringResource(R.string.last_used),
                    value = formatIsoDate(cilt.dateOfLastUsed) ?: stringResource(R.string.not_available),
                )
                SectionTag(title = stringResource(R.string.cilt_status), value = cilt.status)
            }

            CiltDiagramSection(imageUrl = cilt.urlImgLayout)

            Spacer(modifier = Modifier.height(8.dp))

            cilt.sequences.forEachIndexed { index, sequence ->
                SequenceCard(
                    sequence = sequence,
                    navController = navController,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

fun isDateOverdue(
    dateString: String?,
    pattern: String = "yyyy-MM-dd",
): Boolean {
    if (dateString.isNullOrBlank()) return false

    return try {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        val dueDate: Date = formatter.parse(dateString) ?: return false
        val today: Date = formatter.parse(formatter.format(Date())) ?: return false

        dueDate.before(today)
    } catch (e: Exception) {
        false
    }
}

fun formatIsoDate(
    isoString: String?,
    outputPattern: String = "dd-MM-yyyy HH:mm",
): String {
    if (isoString.isNullOrBlank()) return ""

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = inputFormat.parse(isoString)
        val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("UTC")

        outputFormat.format(date ?: return "")
    } catch (e: Exception) {
        ""
    }
}
