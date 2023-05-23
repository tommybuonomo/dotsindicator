package com.tbuonomo.dotsindicatorsample.ui.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tbuonomo.dotsindicatorsample.R.drawable
import com.tbuonomo.dotsindicatorsample.ui.compose.component.TextPlaceholderStyle.Description
import com.tbuonomo.dotsindicatorsample.ui.compose.component.TextPlaceholderStyle.Title

@Composable
fun PagePlaceholderItem() {
    Card(
        modifier = Modifier.wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp)) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
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
