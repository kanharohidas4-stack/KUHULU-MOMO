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
    val halfPrice: Int,
    val fullPrice: Int
)

private val menuItems = listOf(
    MenuItem("Veg Steam Momos", 40, 80),
    MenuItem("Paneer Steam Momos", 50, 100),
    MenuItem("Veg Fried Momos", 50, 100),
    MenuItem("Paneer Fried Momos", 60, 110),
    MenuItem("Veg Kurkure Momos", 60, 110),
    MenuItem("Paneer Kurkure Momos", 70, 130)
)

@Composable
fun KuhuluMomoApp() {
    val isOwner = BuildConfig.APP_TYPE == "OWNER"
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
                                if (isOwner) "Owner App • Your business" else "Delicious momos, delivered to you",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                if (isOwner) {
                    item {
                        SectionCard(
                            "Today's dashboard",
                            "Sales, expenses and profit will appear here."
                        )
                    }
                    items(
                        listOf(
                            "Order management",
                            "Sales and expenses",
                            "Profit / loss",
                            "Stock management"
                        )
                    ) { label ->
                        SectionCard(label, "Coming in a future update")
                    }
                } else {
                    item {
                        SectionCard(
                            "Our menu",
                            "Steam, fried and kurkure momos."
                        )
                    }
                    item {
                        Text(
                            "Item • Half (6 pcs) • Full (12 pcs)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(menuItems) { item ->
                        MenuPriceCard(
                            item = item,
                            halfQuantity = cart["${item.name}|Half"] ?: 0,
                            fullQuantity = cart["${item.name}|Full"] ?: 0,
                            onAddHalf = { cart["${item.name}|Half"] = (cart["${item.name}|Half"] ?: 0) + 1 },
                            onAddFull = { cart["${item.name}|Full"] = (cart["${item.name}|Full"] ?: 0) + 1 }
                        )
                    }
                    item {
                        Text(
                            "Home delivery: within 2 KM",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Your cart ($cartCount)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                if (cartLines.isEmpty()) {
                                    Text("Your cart is empty. Add momos from the menu.")
                                } else {
                                    cartLines.forEach { line ->
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Column(Modifier.weight(1f)) {
                                                Text(line.item.name, fontWeight = FontWeight.SemiBold)
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
                                    Text("Total: ₹$cartTotal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Button(onClick = { showCheckout = true }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Proceed to order")
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Text(
                            "Note: On the next screen, choose WhatsApp or another sharing app to send your order.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }

    if (showCheckout && !isOwner) {
        CheckoutDialog(
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
                context.startActivity(Intent.createChooser(sendIntent, "Send order"))
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
    onDismiss: () -> Unit,
    onShare: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val ready = name.isNotBlank() && phone.length >= 10 && address.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Order details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Your name") }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it.filter(Char::isDigit).take(10) }, label = { Text("Mobile number") }, singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full delivery address") })
                Text("Delivery is available only within 2 KM.")
            }
        },
        confirmButton = {
            Button(onClick = { onShare(name.trim(), phone, address.trim()) }, enabled = ready) {
                Text("Share order")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun MenuPriceCard(
    item: MenuItem,
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
                item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Half • 6 pcs", style = MaterialTheme.typography.bodyMedium)
                    Text("₹${item.halfPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Button(onClick = onAddHalf) { Text("Add (+)  $halfQuantity") }
                }
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text("Full • 12 pcs", style = MaterialTheme.typography.bodyMedium)
                    Text("₹${item.fullPrice}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Button(onClick = onAddFull) { Text("Add (+)  $fullQuantity") }
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
