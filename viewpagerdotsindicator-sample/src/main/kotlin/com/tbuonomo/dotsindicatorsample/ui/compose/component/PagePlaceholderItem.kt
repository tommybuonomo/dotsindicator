package com.tbuonomo.dotsindicatorsample.ui.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tbuonomo.dotsindicatorsample.R.drawable
import com.tbuonomo.dotsindicatorsample.ui.compose.component.TextPlaceholderStyle.Description
import com.tbuonomo.dotsindicatorsample.ui.compose.component.TextPlaceholderStyle.Title

@Composable
fun PagePlaceholderItem() {
    Surface(
        modifier = Modifier.wrapContentHeight(),
        color = Color(0xFFDCEBF7),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 32.dp)) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                painter = painterResource(id = drawable.mountains),
                contentDescription = "Page",
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextPlaceholder(Title, 128.dp)
            Spacer(modifier = Modifier.height(8.dp))
            TextPlaceholder(Description, 0.dp)
            TextPlaceholder(Description, 32.dp)
        }
    }
}
