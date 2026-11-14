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

private data class MenuItem(
    val name: String,
    val hindiName: String,
    val halfPrice: Int,
    val fullPrice: Int
)

private val menuItems = listOf(
    MenuItem("Veg Steam Momos", "वेज स्टीम मोमोज़", 40, 80),
    MenuItem("Paneer Steam Momos", "पनीर स्टीम मोमोज़", 50, 100),
    MenuItem("Veg Fried Momos", "वेज फ्राइड मोमोज़", 50, 100),
    MenuItem("Paneer Fried Momos", "पनीर फ्राइड मोमोज़", 60, 110),
    MenuItem("Veg Kurkure Momos", "वेज कुरकुरे मोमोज़", 60, 110),
    MenuItem("Paneer Kurkure Momos", "पनीर कुरकुरे मोमोज़", 70, 130),
    MenuItem("Veg Chilli Gravy Momos", "वेज चिली ग्रेवी मोमोज़", 80, 150),
    MenuItem("Paneer Chilli Gravy Momos", "पनीर चिली ग्रेवी मोमोज़", 90, 170)
)

@Composable
fun KuhuluMomoApp() {
    val isOwner = BuildConfig.APP_TYPE == "OWNER"
    var hindi by remember { mutableStateOf(true) }

    MaterialTheme {
        Scaffold { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("KUHULU MOMO", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(
                                if (isOwner) {
                                    if (hindi) "Owner App • आपका बिज़नेस" else "Owner App • Your business"
                                } else {
                                    if (hindi) "स्वादिष्ट मोमोज़, सीधे आपके घर" else "Delicious momos, delivered to you"
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        TextButton(onClick = { hindi = !hindi }) {
                            Text(if (hindi) "English" else "हिंदी")
                        }
                    }
                }

                if (isOwner) {
                    item {
                        SectionCard(
                            if (hindi) "आज का डैशबोर्ड" else "Today's dashboard",
                            if (hindi) "बिक्री, खर्च और मुनाफा यहाँ देख सकेंगे।" else "Sales, expenses and profit will appear here."
                        )
                    }
                    items(
                        listOf(
                            if (hindi) "ऑर्डर मैनेजमेंट" else "Order management",
                            if (hindi) "बिक्री और खर्च" else "Sales and expenses",
                            if (hindi) "मुनाफा / नुकसान" else "Profit / loss",
                            if (hindi) "स्टॉक मैनेजमेंट" else "Stock management"
                        )
                    ) { label ->
                        SectionCard(label, if (hindi) "अगले अपडेट में आएगा" else "Coming in a future update")
                    }
                } else {
                    item {
                        SectionCard(
                            if (hindi) "हमारा मेन्यू" else "Our menu",
                            if (hindi) "स्टीम, फ्राइड, कुरकुरे और चिली मोमोज़।" else "Steam, fried, kurkure and chilli momos."
                        )
                    }
                    item {
                        Text(
                            if (hindi) "आइटम • हाफ (6 पीस) • फुल (12 पीस)" else "Item • Half (6 pcs) • Full (12 pcs)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(menuItems) { item ->
                        MenuPriceCard(item = item, hindi = hindi)
                    }
                    item {
                        Text(
                            if (hindi) "होम डिलीवरी: 2 KM के अंदर" else "Home delivery: within 2 KM",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    item {
                        Text(
                            if (hindi) "ऑनलाइन ऑर्डर की सुविधा अगले अपडेट में जोड़ी जाएगी।" else "Online ordering will be added in a future update.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuPriceCard(item: MenuItem, hindi: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                if (hindi) item.hindiName else item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(if (hindi) "हाफ • 6 पीस" else "Half • 6 pcs", style = MaterialTheme.typography.bodyMedium)
                    Text("₹${item.halfPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(if (hindi) "फुल • 12 पीस" else "Full • 12 pcs", style = MaterialTheme.typography.bodyMedium)
                    Text("₹${item.fullPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
