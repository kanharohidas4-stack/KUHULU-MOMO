package com.kuhulumomo

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
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
    val cart = remember { mutableStateMapOf<String, Int>() }
    var showCheckout by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cartLines = menuItems.flatMap { item ->
        listOf(
            Triple(item, "Half", item.halfPrice),
            Triple(item, "Full", item.fullPrice)
        )
    }.mapNotNull { (item, size, price) ->
        val key = "${item.name}|$size"
        val quantity = cart[key] ?: 0
        if (quantity > 0) CartLine(item, size, price, quantity, key) else null
    }
    val cartTotal = cartLines.sumOf { it.price * it.quantity }
    val cartCount = cartLines.sumOf { it.quantity }

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
                        MenuPriceCard(
                            item = item,
                            hindi = hindi,
                            halfQuantity = cart["${item.name}|Half"] ?: 0,
                            fullQuantity = cart["${item.name}|Full"] ?: 0,
                            onAddHalf = { cart["${item.name}|Half"] = (cart["${item.name}|Half"] ?: 0) + 1 },
                            onAddFull = { cart["${item.name}|Full"] = (cart["${item.name}|Full"] ?: 0) + 1 }
                        )
                    }
                    item {
                        Text(
                            if (hindi) "होम डिलीवरी: 2 KM के अंदर" else "Home delivery: within 2 KM",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(if (hindi) "आपका कार्ट ($cartCount)" else "Your cart ($cartCount)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                if (cartLines.isEmpty()) {
                                    Text(if (hindi) "अभी कार्ट खाली है। मेन्यू से मोमोज़ जोड़ें।" else "Your cart is empty. Add momos from the menu.")
                                } else {
                                    cartLines.forEach { line ->
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Column(Modifier.weight(1f)) {
                                                Text(if (hindi) line.item.hindiName else line.item.name, fontWeight = FontWeight.SemiBold)
                                                Text("${line.size} • ₹${line.price} × ${line.quantity} = ₹${line.price * line.quantity}")
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                TextButton(onClick = {
                                                    if (line.quantity <= 1) cart.remove(line.key) else cart[line.key] = line.quantity - 1
                                                }) { Text("−") }
                                                Text("${line.quantity}")
                                                TextButton(onClick = { cart[line.key] = line.quantity + 1 }) { Text("+") }
                                            }
                                        }
                                    }
                                    HorizontalDivider()
                                    Text(if (hindi) "कुल: ₹$cartTotal" else "Total: ₹$cartTotal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Button(onClick = { showCheckout = true }, modifier = Modifier.fillMaxWidth()) {
                                        Text(if (hindi) "ऑर्डर आगे बढ़ाएँ" else "Proceed to order")
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Text(
                            if (hindi) "नोट: ऑर्डर भेजने के लिए अगली स्क्रीन पर WhatsApp या कोई शेयर ऐप चुनें।" else "Note: On the next screen, choose WhatsApp or another sharing app to send your order.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (showCheckout && !isOwner) {
        CheckoutDialog(
            hindi = hindi,
            onDismiss = { showCheckout = false },
            onShare = { customerName, phone, address ->
                val orderText = buildString {
                    appendLine("KUHULU MOMO ORDER")
                    appendLine("Name: $customerName")
                    appendLine("Phone: $phone")
                    appendLine("Address: $address")
                    appendLine()
                    cartLines.forEach { appendLine("${it.item.name} (${it.size}, ${if (it.size == "Half") 6 else 12} pcs) x ${it.quantity} = ₹${it.price * it.quantity}") }
                    appendLine("Total: ₹$cartTotal")
                    appendLine("Delivery requested within 2 KM.")
                }
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, orderText)
                }
                context.startActivity(Intent.createChooser(sendIntent, if (hindi) "ऑर्डर भेजें" else "Send order"))
                showCheckout = false
            }
        )
    }
}

private data class CartLine(
    val item: MenuItem,
    val size: String,
    val price: Int,
    val quantity: Int,
    val key: String
)

@Composable
private fun CheckoutDialog(
    hindi: Boolean,
    onDismiss: () -> Unit,
    onShare: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val ready = name.isNotBlank() && phone.length >= 10 && address.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (hindi) "ऑर्डर की जानकारी" else "Order details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(if (hindi) "आपका नाम" else "Your name") }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it.filter(Char::isDigit).take(10) }, label = { Text(if (hindi) "मोबाइल नंबर" else "Mobile number") }, singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(if (hindi) "डिलीवरी का पूरा पता" else "Full delivery address") })
                Text(if (hindi) "डिलीवरी केवल 2 KM के अंदर उपलब्ध है।" else "Delivery is available only within 2 KM.")
            }
        },
        confirmButton = {
            Button(onClick = { onShare(name.trim(), phone, address.trim()) }, enabled = ready) {
                Text(if (hindi) "ऑर्डर शेयर करें" else "Share order")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(if (hindi) "रद्द करें" else "Cancel") }
        }
    )
}

@Composable
private fun MenuPriceCard(
    item: MenuItem,
    hindi: Boolean,
    halfQuantity: Int,
    fullQuantity: Int,
    onAddHalf: () -> Unit,
    onAddFull: () -> Unit
) {
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
                Column(Modifier.weight(1f)) {
                    Text(if (hindi) "हाफ • 6 पीस" else "Half • 6 pcs", style = MaterialTheme.typography.bodyMedium)
                    Text("₹${item.halfPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Button(onClick = onAddHalf) { Text(if (hindi) "जोड़ें (+)  $halfQuantity" else "Add (+)  $halfQuantity") }
                }
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(if (hindi) "फुल • 12 पीस" else "Full • 12 pcs", style = MaterialTheme.typography.bodyMedium)
                    Text("₹${item.fullPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Button(onClick = onAddFull) { Text(if (hindi) "जोड़ें (+)  $fullQuantity" else "Add (+)  $fullQuantity") }
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
