package com.kuhulumomo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Red = Color(0xFFC71920)
private val DeepRed = Color(0xFF8F1018)
private val Yellow = Color(0xFFFFC928)
private val Ink = Color(0xFF201B1B)
private val Cream = Color(0xFFFFF8F0)
private val Muted = Color(0xFF777070)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KuhuluMomoApp() }
    }
}

private data class MenuItem(val name: String, val halfPrice: Int, val fullPrice: Int, val emoji: String)
private val menuItems = listOf(
    MenuItem("Veg Steam Momos", 40, 80, "🥟"),
    MenuItem("Paneer Steam Momos", 50, 100, "🥟"),
    MenuItem("Veg Fried Momos", 50, 100, "🍽️"),
    MenuItem("Paneer Fried Momos", 60, 110, "🍽️"),
    MenuItem("Veg Kurkure Momos", 60, 110, "✨"),
    MenuItem("Paneer Kurkure Momos", 70, 130, "✨")
)

@Composable
fun KuhuluMomoApp() {
    val isOwner = BuildConfig.APP_TYPE == "OWNER"
    val cart = remember { mutableStateMapOf<String, Int>() }
    var showCheckout by remember { mutableStateOf(false) }
    var selectedOwnerSection by remember { mutableStateOf("Overview") }
    val context = LocalContext.current

    val cartLines = menuItems.flatMap { item ->
        listOf(Triple(item, "Half", item.halfPrice), Triple(item, "Full", item.fullPrice))
    }.mapNotNull { (item, size, price) ->
        val key = "${item.name}|$size"
        val quantity = cart[key] ?: 0
        if (quantity > 0) CartLine(item, size, price, quantity, key) else null
    }
    val cartTotal = cartLines.sumOf { it.price * it.quantity }
    val cartCount = cartLines.sumOf { it.quantity }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Red,
            onPrimary = Color.White,
            secondary = Yellow,
            background = Cream,
            surface = Color.White,
            onSurface = Ink
        )
    ) {
        Scaffold(containerColor = Cream) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { AppTopBar(isOwner) }
                if (isOwner) {
                    item {
                        OwnerHero()
                    }
                    item {
                        Text("BUSINESS OVERVIEW", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard("Today's sales", "₹0", "💰", Modifier.weight(1f))
                            MetricCard("Orders", "0", "🧾", Modifier.weight(1f))
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard("Expenses", "₹0", "📉", Modifier.weight(1f))
                            MetricCard("Net profit", "₹0", "📈", Modifier.weight(1f))
                        }
                    }
                    item {
                        Text("MANAGE YOUR BUSINESS", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                    }
                    items(listOf(
                        Triple("Orders", "View and update customer orders", "📦"),
                        Triple("Sales & expenses", "Record daily income and costs", "💳"),
                        Triple("Profit / loss", "Review your business performance", "📊"),
                        Triple("Stock management", "Track ingredients and supplies", "🧺"),
                        Triple("Menu management", "Review items and plate prices", "📋")
                    )) { (title, detail, emoji) ->
                        OwnerActionCard(title, detail, emoji, selectedOwnerSection == title) {
                            selectedOwnerSection = title
                        }
                    }
                    if (selectedOwnerSection != "Overview") {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0D1)),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Text(selectedOwnerSection, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("This section is ready for the next data and Firebase integration step.", color = Muted)
                                }
                            }
                        }
                    }
                    item {
                        Text("Your Owner App is the business control panel. Sales and profit figures will populate after records are connected.", color = Muted, fontSize = 12.sp)
                    }
                } else {
                    item { CustomerHero() }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Our menu", fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Ink)
                                Text("Freshly made. Full of flavour.", color = Muted, fontSize = 13.sp)
                            }
                            Surface(color = Color(0xFFFFE6A0), shape = RoundedCornerShape(30.dp)) {
                                Text("6 varieties", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp), color = DeepRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
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
                    item { DeliveryTrustStrip() }
                    item {
                        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Your cart", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                                    Surface(color = Red, shape = CircleShape) {
                                        Text("$cartCount items", color = Color.White, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (cartLines.isEmpty()) {
                                    Text("Your cart is empty. Add your favourite momos above.", color = Muted)
                                } else {
                                    cartLines.forEach { line ->
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                            Column(Modifier.weight(1f)) {
                                                Text(line.item.name, fontWeight = FontWeight.SemiBold)
                                                Text("${line.size} • ₹${line.price} × ${line.quantity}", color = Muted, fontSize = 12.sp)
                                            }
                                            TextButton(onClick = {
                                                if (line.quantity <= 1) cart.remove(line.key) else cart[line.key] = line.quantity - 1
                                            }) { Text("−", fontSize = 18.sp) }
                                            Text("${line.quantity}", fontWeight = FontWeight.Bold)
                                            TextButton(onClick = { cart[line.key] = line.quantity + 1 }) { Text("+", fontSize = 18.sp) }
                                        }
                                    }
                                    HorizontalDivider()
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        Text("₹$cartTotal", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Red)
                                    }
                                    Button(
                                        onClick = { showCheckout = true },
                                        modifier = Modifier.fillMaxWidth().height(52.dp),
                                        shape = RoundedCornerShape(15.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                                    ) { Text("ORDER NOW  →", fontWeight = FontWeight.ExtraBold) }
                                }
                            }
                        }
                    }
                    item { Text("Simple ordering • Friendly service • Affordable plates", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Muted, fontSize = 12.sp) }
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
                context.startActivity(Intent.createChooser(sendIntent, "Send your KUHULU MOMO order"))
                showCheckout = false
            }
        )
    }
}

@Composable
private fun AppTopBar(isOwner: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        BrandMark(Modifier.size(52.dp))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("KUHULU MOMO", fontWeight = FontWeight.Black, fontSize = 20.sp, color = DeepRed, letterSpacing = 0.3.sp)
            Text(if (isOwner) "OWNER • BUSINESS APP" else "FRESH • TASTY • AFFORDABLE", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Muted, letterSpacing = 1.sp)
        }
        Surface(color = if (isOwner) Color(0xFFFFE7A3) else Color(0xFFFFE4E4), shape = RoundedCornerShape(30.dp)) {
            Text(if (isOwner) "OWNER" else "CUSTOMER", color = DeepRed, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
        }
    }
}

@Composable
private fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Red, DeepRed))),
        contentAlignment = Alignment.Center
    ) {
        Text("🥟", fontSize = 29.sp)
        Box(Modifier.align(Alignment.BottomEnd).size(13.dp).clip(CircleShape).background(Yellow))
    }
}

@Composable
private fun CustomerHero() {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(DeepRed, Red, Color(0xFFDF3B28))))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("HELLO, MOMO LOVER!", color = Yellow, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, letterSpacing = 1.2.sp)
                Text("Happiness in
every bite.", color = Color.White, fontSize = 29.sp, lineHeight = 33.sp, fontWeight = FontWeight.Black)
                Text("Delicious momos at friendly prices.", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            }
            Text("🥟", fontSize = 76.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Surface(color = Yellow, shape = RoundedCornerShape(30.dp)) {
                Text("EXPLORE MENU  ↓", color = DeepRed, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp))
            }
            Text("Made fresh • Served hot", color = Color.White, fontSize = 10.sp)
        }
    }
}

@Composable
private fun OwnerHero() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(DeepRed, Red, Color(0xFFDA4A2E))))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("WELCOME BACK", color = Yellow, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.3.sp)
            Text("Your business.
Your control.", color = Color.White, fontSize = 25.sp, lineHeight = 29.sp, fontWeight = FontWeight.Black)
            Text("Manage KUHULU MOMO in one place.", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
        }
        Text("👨‍🍳", fontSize = 60.sp)
    }
}

@Composable
private fun MetricCard(title: String, value: String, emoji: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(emoji, fontSize = 22.sp)
            Text(value, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = Red)
            Text(title, fontSize = 12.sp, color = Muted, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun OwnerActionCard(title: String, detail: String, emoji: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFFFFE8E3) else Color.White)
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFFFF0D2)), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(detail, color = Muted, fontSize = 12.sp)
            }
            Text("›", color = Red, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MenuPriceCard(item: MenuItem, halfQuantity: Int, fullQuantity: Int, onAddHalf: () -> Unit, onAddFull: () -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(52.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFFFF0D7)), contentAlignment = Alignment.Center) {
                    Text(item.emoji, fontSize = 29.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Ink)
                    Text("Freshly prepared • Delicious", color = Muted, fontSize = 11.sp)
                }
                Text("★ 4.8", color = Color(0xFFB87900), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PriceOption("HALF • 6 PCS", item.halfPrice, halfQuantity, onAddHalf, Modifier.weight(1f))
                PriceOption("FULL • 12 PCS", item.fullPrice, fullQuantity, onAddFull, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PriceOption(label: String, price: Int, quantity: Int, onAdd: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Color(0xFFFFF7EF)).padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Muted)
        Text("₹$price", fontSize = 21.sp, fontWeight = FontWeight.Black, color = DeepRed)
        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth().height(38.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
            shape = RoundedCornerShape(11.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Red)
        ) { Text(if (quantity == 0) "ADD  +" else "ADD  +$quantity", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold) }
    }
}

@Composable
private fun DeliveryTrustStrip() {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE9B0))) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("🛵", fontSize = 27.sp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("HOME DELIVERY", fontWeight = FontWeight.ExtraBold, color = DeepRed, fontSize = 12.sp)
                Text("Within 2 KM", color = Ink, fontSize = 12.sp)
            }
            Text("🛡️", fontSize = 25.sp)
            Spacer(Modifier.width(6.dp))
            Column {
                Text("FRESH", fontWeight = FontWeight.ExtraBold, color = DeepRed, fontSize = 12.sp)
                Text("Made with care", color = Ink, fontSize = 10.sp)
            }
        }
    }
}

private data class CartLine(val item: MenuItem, val size: String, val price: Int, val quantity: Int, val key: String)

@Composable
private fun CheckoutDialog(onDismiss: () -> Unit, onShare: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val ready = name.isNotBlank() && phone.length == 10 && address.isNotBlank()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your delivery details", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full name") }, singleLine = true)
                OutlinedTextField(value = phone, onValueChange = { phone = it.filter(Char::isDigit).take(10) }, label = { Text("10-digit mobile number") }, singleLine = true)
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full delivery address") })
                Text("Delivery is available within 2 KM.", color = Muted, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = { onShare(name.trim(), phone, address.trim()) }, enabled = ready) { Text("SHARE ORDER") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
