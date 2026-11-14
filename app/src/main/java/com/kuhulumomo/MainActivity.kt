package com.kuhulumomo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KuhuluMomoApp() }
    }
}

@Composable
fun KuhuluMomoApp() {
    val isOwner = BuildConfig.APP_TYPE == "OWNER"
    var hindi by remember { mutableStateOf(true) }
    val title = if (hindi) "KUHULU MOMO" else "KUHULU MOMO"
    val subtitle = when {
        isOwner && hindi -> "Owner App • आपका बिज़नेस"
        isOwner -> "Owner App • Your business"
        hindi -> "स्वादिष्ट मोमोज़, सीधे आपके घर"
        else -> "Delicious momos, delivered to you"
    }

    MaterialTheme {
        Scaffold { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                        }
                        TextButton(onClick = { hindi = !hindi }) {
                            Text(if (hindi) "English" else "हिंदी")
                        }
                    }
                }
                if (isOwner) {
                    item { SectionCard(if (hindi) "आज का डैशबोर्ड" else "Today's dashboard",
                        if (hindi) "बिक्री, खर्च और मुनाफा यहाँ देख सकेंगे।" else "Sales, expenses and profit will appear here.") }
                    items(listOf(
                        if (hindi) "ऑर्डर मैनेजमेंट" else "Order management",
                        if (hindi) "बिक्री और खर्च" else "Sales and expenses",
                        if (hindi) "मुनाफा / नुकसान" else "Profit / loss",
                        if (hindi) "स्टॉक मैनेजमेंट" else "Stock management"
                    )) { label -> SectionCard(label, if (hindi) "जल्द आ रहा है" else "Coming soon") }
                } else {
                    item { SectionCard(if (hindi) "हमारा मेन्यू" else "Our menu",
                        if (hindi) "स्टीम, फ्राइड, कुरकुरे और चिली मोमोज़।" else "Steam, fried, kurkure and chilli momos.") }
                    items(listOf(
                        "Veg Steam Momos",
                        "Paneer Steam Momos",
                        "Veg Fried Momos",
                        "Paneer Fried Momos",
                        "Veg Kurkure Momos",
                        "Paneer Kurkure Momos",
                        "Chilli Momos"
                    )) { item -> SectionCard(item, if (hindi) "ऑर्डर सुविधा जल्द आ रही है" else "Ordering coming soon") }
                    item { Text(if (hindi) "होम डिलीवरी: 2 KM के अंदर" else "Home delivery: within 2 KM",
                        style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@Composable
fun SectionCard(title: String, detail: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(detail, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
