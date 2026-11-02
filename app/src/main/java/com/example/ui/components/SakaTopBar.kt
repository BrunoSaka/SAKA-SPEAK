package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SakaBorderGold
import com.example.ui.theme.SakaCreamMuted
import com.example.ui.theme.SakaDark
import com.example.ui.theme.SakaGold
import com.example.ui.theme.SakaMatchaLight

@Composable
fun SakaTopBar(
    modifier: Modifier = Modifier,
    actions: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SakaDark.copy(alpha = 0.95f))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("app_logo_title")
            ) {
                // User Logo Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, SakaGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_user_logo),
                        contentDescription = "Logo SAKA SPEAK",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = SakaGold, fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp)) {
                                append("SAKA ")
                            }
                            withStyle(SpanStyle(color = SakaMatchaLight, fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp)) {
                                append("SPEAK")
                            }
                        },
                        fontSize = 17.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "THINK • SPEAK • IMPACT",
                        fontSize = 9.sp,
                        color = SakaCreamMuted,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    actions()
                }
            }
        }

        HorizontalDivider(
            color = SakaBorderGold.copy(alpha = 0.25f),
            thickness = 1.dp
        )
    }
}
