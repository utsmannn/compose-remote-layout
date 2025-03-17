Compose Remote Layout provides a powerful event handling system that allows you to create
interactive UIs that respond to user actions. This guide explains how to implement and manage click
events in your dynamic layouts.

## Understanding Action Binding

Action binding allows your JSON-defined layouts to respond to user interactions in ways that you
define in your Kotlin code. This creates an end-to-end connection from your remote layouts to your
application logic.

Key benefits include:

- **Separation of concerns**: Layout defines appearance, Kotlin code defines behavior
- **Dynamic behavior**: Change both UI and interactions without app updates
- **Type safety**: Handle events in typed Kotlin code, not in JSON
- **Integration with app logic**: Connect actions to your existing navigation, state management,
  etc.

## Basic Click Handling

### Step 1: Define Clickable Elements in JSON

You can make components clickable by adding a `clickId` in one of two ways:

**For Button components**, use the `clickId` property directly:

```json
{
  "button": {
    "content": "Sign In",
    "clickId": "login_button"
  }
}
```

**For other components**, add `clickId` to the base modifier:

```json
{
  "card": {
    "modifier": {
      "base": {
        "clickId": "open_profile",
        "padding": {
          "all": 16
        }
      }
    },
    "children": [
      {
        "text": {
          "content": "View Profile"
        }
      }
    ]
  }
}
```

### Step 2: Handle Click Events in Kotlin

The `DynamicLayout` composable accepts an `onClickHandler` parameter, which is a function that
receives the clickId:

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val layoutJson = """
    {
      "column": {
        "modifier": {
          "base": {
            "fillMaxWidth": true,
            "padding": {
              "all": 24
            }
          },
          "verticalArrangement": "spaceBetween"
        },
        "children": [
          {
            "text": {
              "content": "Welcome Back",
              "fontSize": 24,
              "fontWeight": "bold",
              "textAlign": "center"
            }
          },
          {
            "button": {
              "content": "Sign In with Email",
              "clickId": "login_email",
              "modifier": {
                "base": {
                  "fillMaxWidth": true
                }
              }
            }
          },
          {
            "button": {
              "content": "Sign In with Google",
              "clickId": "login_google",
              "modifier": {
                "base": {
                  "fillMaxWidth": true
                }
              }
            }
          },
          {
            "text": {
              "content": "Forgot Password?",
              "textAlign": "center",
              "modifier": {
                "base": {
                  "clickId": "forgot_password"
                }
              }
            }
          }
        ]
      }
    }
    """

    val component = createLayoutComponent(layoutJson)

    DynamicLayout(
        component = component,
        onClickHandler = { clickId ->
            // Handle clicks based on ID
            when (clickId) {
                "login_email" -> viewModel.navigateToEmailLogin()
                "login_google" -> viewModel.initiateGoogleLogin()
                "forgot_password" -> viewModel.navigateToPasswordReset()
            }
        }
    )
}
```

## Advanced Action Patterns

### Passing Parameters through Click IDs

You can encode additional data in click IDs using a structured format:

```json
{
  "text": {
    "content": "View Details",
    "modifier": {
      "base": {
        "clickId": "view_product:123"
      }
    }
  }
}
```

Then extract the parameters in your click handler:

```kotlin
DynamicLayout(
    component = component,
    onClickHandler = { clickId ->
        if (clickId.startsWith("view_product:")) {
            val productId = clickId.split(":")[1]
            navigateToProductDetail(productId)
        }
    }
)
```

Common patterns include:

- Using colon as separator: `"action:parameter"`
- Multiple parameters: `"action:param1:param2"`
- Key-value pairs: `"action:id=123:type=product"`

### Click Actions with Bind Values

Combine click actions with bind values for dynamic behavior:

```kotlin
@Composable
fun ProductListScreen(products: List<Product>) {
    val bindsValue = remember { BindsValue() }

    // Update the product list in binds
    LaunchedEffect(products) {
        // We'll build a string representing product rows
        val productRows = products.mapIndexed { index, product ->
            """
            {
              "card": {
                "modifier": {
                  "base": {
                    "clickId": "view_product:${product.id}",
                    "padding": {
                      "all": 12
                    },
                    "margin": {
                      "bottom": 8
                    }
                  }
                },
                "children": [
                  {
                    "row": {
                      "children": [
                        {
                          "text": {
                            "content": "${product.name}",
                            "fontSize": 16,
                            "fontWeight": "medium"
                          }
                        },
                        {
                          "text": {
                            "content": "$${product.price}",
                            "fontSize": 16,
                            "textAlign": "end"
                          }
                        }
                      ]
                    }
                  }
                ]
              }
            }
            """
        }.joinToString(",\n")

        // Set the products JSON as a bind value
        bindsValue.setValue("productItems", productRows)
    }

    // This layout uses bind value to insert dynamically built product items
    val layoutJson = """
    {
      "column": {
        "modifier": {
          "base": {
            "fillMaxWidth": true,
            "padding": {
              "all": 16
            }
          }
        },
        "children": [
          {
            "text": {
              "content": "Products",
              "fontSize": 24,
              "fontWeight": "bold",
              "modifier": {
                "base": {
                  "padding": {
                    "bottom": 16
                  }
                }
              }
            }
          },
          {productItems}
        ]
      }
    }
    """

    val component = createLayoutComponent(layoutJson)

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            if (clickId.startsWith("view_product:")) {
                val productId = clickId.split(":")[1]
                navigateToProductDetail(productId)
            }
        }
    )
}
```

### Implementing a Counter

A simple interactive counter component with bind values and actions:

```kotlin
@Composable
fun CounterExample() {
    var count by remember { mutableStateOf(0) }
    val bindsValue = remember { BindsValue() }

    // Update binds when count changes
    LaunchedEffect(count) {
        bindsValue.setValue("count", count.toString())

        // Set color based on count value
        val color = when {
            count > 10 -> "#00AA00"  // Green for high values
            count < 0 -> "#AA0000"   // Red for negative values
            else -> "#000000"        // Black for normal values
        }
        bindsValue.setValue("countColor", color)
    }

    val countLayoutJson = """
    {
      "column": {
        "modifier": {
          "base": {
            "fillMaxWidth": true,
            "padding": {
              "all": 24
            }
          },
          "horizontalAlignment": "center",
          "verticalArrangement": "spaceBetween"
        },
        "children": [
          {
            "text": {
              "content": "Counter Example",
              "fontSize": 24,
              "fontWeight": "bold"
            }
          },
          {
            "text": {
              "content": "{count}",
              "fontSize": 64,
              "fontWeight": "bold",
              "color": "{countColor}",
              "modifier": {
                "base": {
                  "padding": {
                    "vertical": 32
                  }
                }
              }
            }
          },
          {
            "row": {
              "modifier": {
                "base": {
                  "fillMaxWidth": true
                },
                "horizontalArrangement": "spaceEvenly"
              },
              "children": [
                {
                  "button": {
                    "content": "-",
                    "clickId": "decrement",
                    "fontSize": 20
                  }
                },
                {
                  "button": {
                    "content": "+",
                    "clickId": "increment",
                    "fontSize": 20
                  }
                }
              ]
            }
          },
          {
            "button": {
              "content": "Reset",
              "clickId": "reset",
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "padding": {
                    "vertical": 8
                  }
                }
              }
            }
          }
        ]
      }
    }
    """

    val component = createLayoutComponent(countLayoutJson)

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            when (clickId) {
                "increment" -> count++
                "decrement" -> count--
                "reset" -> count = 0
            }
        }
    )
}
```

## Integration with App Architecture

### ViewModel Integration

Delegate click handling to a ViewModel for clean architecture:

```kotlin
@Composable
fun ProductScreen(viewModel: ProductViewModel) {
    // Observe layout JSON from ViewModel
    val layoutJson by viewModel.layoutJson.collectAsState()

    // Observe bound values from ViewModel
    val bindsValue by viewModel.bindsValue.collectAsState()

    val component = createLayoutComponent(layoutJson)

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId -> viewModel.handleClick(clickId) }
    )
}

class ProductViewModel : ViewModel() {
    // State holders
    private val _layoutJson = MutableStateFlow("")
    val layoutJson: StateFlow<String> = _layoutJson

    private val _bindsValue = MutableStateFlow(BindsValue())
    val bindsValue: StateFlow<BindsValue> = _bindsValue

    init {
        // Load layout
        viewModelScope.launch {
            _layoutJson.value = repository.getProductLayoutJson()
            refreshProducts()
        }
    }

    private fun refreshProducts() {
        viewModelScope.launch {
            val products = repository.getProducts()

            // Update binds with product data
            val currentBinds = _bindsValue.value
            products.forEachIndexed { index, product ->
                currentBinds.setValue("product_${index}_name", product.name)
                currentBinds.setValue("product_${index}_price", "$${product.price}")
                currentBinds.setValue("product_${index}_id", product.id)
            }
            currentBinds.setValue("productCount", products.size.toString())

            // Trigger update
            _bindsValue.value = currentBinds
        }
    }

    fun handleClick(clickId: String) {
        when {
            clickId.startsWith("view_product:") -> {
                val productId = clickId.split(":")[1]
                navigateToProductDetail(productId)
            }
            clickId == "refresh_products" -> {
                refreshProducts()
            }
            clickId == "filter_products" -> {
                showFilterDialog()
            }
            // Handle other clicks
        }
    }

    private fun navigateToProductDetail(productId: String) {
        // Navigation logic
    }

    private fun showFilterDialog() {
        // Dialog logic
    }
}
```

### Navigation Integration

Connect click events to navigation actions:

```kotlin
@Composable
fun NavigationMenuScreen(navController: NavController) {
    val menuLayoutJson = """
    {
      "column": {
        "modifier": {
          "base": {
            "fillMaxWidth": true,
            "padding": {
              "all": 16
            }
          }
        },
        "children": [
          {
            "text": {
              "content": "Main Menu",
              "fontSize": 24,
              "fontWeight": "bold",
              "modifier": {
                "base": {
                  "padding": {
                    "bottom": 16
                  }
                }
              }
            }
          },
          {
            "card": {
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "clickId": "nav_home",
                  "padding": {
                    "all": 16
                  },
                  "margin": {
                    "bottom": 8
                  }
                }
              },
              "children": [
                {
                  "text": {
                    "content": "Home",
                    "fontSize": 18
                  }
                }
              ]
            }
          },
          {
            "card": {
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "clickId": "nav_profile",
                  "padding": {
                    "all": 16
                  },
                  "margin": {
                    "bottom": 8
                  }
                }
              },
              "children": [
                {
                  "text": {
                    "content": "Profile",
                    "fontSize": 18
                  }
                }
              ]
            }
          },
          {
            "card": {
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "clickId": "nav_settings",
                  "padding": {
                    "all": 16
                  },
                  "margin": {
                    "bottom": 8
                  }
                }
              },
              "children": [
                {
                  "text": {
                    "content": "Settings",
                    "fontSize": 18
                  }
                }
              ]
            }
          },
          {
            "button": {
              "content": "Logout",
              "clickId": "logout",
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "margin": {
                    "top": 16
                  }
                }
              }
            }
          }
        ]
      }
    }
    """

    val component = createLayoutComponent(menuLayoutJson)

    DynamicLayout(
        component = component,
        onClickHandler = { clickId ->
            when (clickId) {
                "nav_home" -> navController.navigate("home")
                "nav_profile" -> navController.navigate("profile")
                "nav_settings" -> navController.navigate("settings")
                "logout" -> {
                    // Perform logout operations
                    AuthManager.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
        }
    )
}
```

### State Management Integration

Integrate with state management solutions like MVI/Redux:

```kotlin
@Composable
fun ShoppingCartScreen(store: ReduxStore<AppState, AppAction>) {
    // Observe state from the store
    val state by store.state.collectAsState()

    // Create bind values from the state
    val bindsValue = remember { BindsValue() }

    // Update binds when state changes
    LaunchedEffect(state.cart) {
        bindsValue.setValue("itemCount", state.cart.items.size.toString())
        bindsValue.setValue("totalPrice", "$${state.cart.totalPrice}")
        bindsValue.setValue("isCheckoutEnabled", state.cart.canCheckout().toString())
    }

    val component = createLayoutComponent(cartLayoutJson)

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            // Dispatch actions to the store based on clicks
            when {
                clickId == "checkout" -> {
                    store.dispatch(CartAction.Checkout)
                }
                clickId == "clear_cart" -> {
                    store.dispatch(CartAction.ClearCart)
                }
                clickId.startsWith("remove_item:") -> {
                    val itemId = clickId.split(":")[1]
                    store.dispatch(CartAction.RemoveItem(itemId))
                }
                clickId.startsWith("quantity_plus:") -> {
                    val itemId = clickId.split(":")[1]
                    store.dispatch(CartAction.IncrementQuantity(itemId))
                }
                clickId.startsWith("quantity_minus:") -> {
                    val itemId = clickId.split(":")[1]
                    store.dispatch(CartAction.DecrementQuantity(itemId))
                }
            }
        }
    )
}
```

## Custom Components with Click Events

When creating custom components, you can handle clicks internally or pass them to the parent:

```kotlin
// Register a custom product card component
CustomNodes.register("product_card") { param ->
    val name = param.data["name"] ?: "Unknown Product"
    val price = param.data["price"] ?: "$0.00"
    val imageUrl = param.data["imageUrl"]
    val productId = param.data["id"] ?: ""

    Card(
        modifier = param.modifier
            .clickable {
                // Pass click to parent handler with product ID
                param.onClickHandler("view_product:$productId")
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Product image, name, and price...

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // These buttons handle their own click events
                Button(
                    onClick = {
                        // Internal handling for immediate add to cart
                        CartManager.addItem(productId)
                    }
                ) {
                    Text("Add to Cart")
                }

                // This button passes the click to parent
                Button(
                    onClick = {
                        param.onClickHandler("wishlist_add:$productId")
                    }
                ) {
                    Text("Wishlist")
                }
            }
        }
    }
}

// Usage in JSON
val json = """
{
  "product_card": {
    "name": "Premium Headphones",
    "price": "$149.99",
    "imageUrl": "https://example.com/headphones.jpg",
    "id": "prod-123"
  }
}
"""
```

## Best Practices

### 1. Use Consistent Naming Conventions

Establish a standard pattern for your click IDs:

```kotlin
// Consistent naming convention examples:
// Action-based: verb_object
"add_to_cart"
"view_details"
"submit_form"

// Screen-based: screen_action
"checkout_continue"
"profile_edit"

// With parameters: action:parameter
"remove_item:12345"
"select_category:electronics"
```

### 2. Organize Click Handlers

For complex screens, organize click handling by category:

```kotlin
// In your ViewModel or Controller
fun handleClick(clickId: String) {
    when {
        // Navigation actions
        clickId.startsWith("nav_") -> handleNavigation(clickId)

        // Item interactions
        clickId.startsWith("item_") -> handleItemAction(clickId)

        // Form actions
        clickId.startsWith("form_") -> handleFormAction(clickId)

        // Misc actions
        else -> handleMiscAction(clickId)
    }
}

private fun handleNavigation(clickId: String) {
    when (clickId) {
        "nav_home" -> navigator.navigateTo(Screen.Home)
        "nav_profile" -> navigator.navigateTo(Screen.Profile)
        // More navigation handlers...
    }
}

private fun handleItemAction(clickId: String) {
    // Extract the action and item ID
    val parts = clickId.split("_", ":")
    if (parts.size >= 3) {
        val action = parts[1]
        val itemId = parts[2]

        when (action) {
            "view" -> viewItem(itemId)
            "edit" -> editItem(itemId)
            "delete" -> deleteItem(itemId)
            // More item actions...
        }
    }
}

// Additional handler methods...
```

### 3. Fail Gracefully

Handle unexpected click IDs gracefully:

```kotlin
fun handleClick(clickId: String) {
    try {
        when {
            clickId.startsWith("view_product:") -> {
                val productId = clickId.split(":")[1]
                navigateToProductDetail(productId)
            }
            // Other click handlers...
            else -> {
                // Log unknown clickId for debugging
                analytics.logEvent("unknown_click_id", mapOf("id" to clickId))
            }
        }
    } catch (e: Exception) {
        // Log error
        analytics.logError("click_handler_error", e)
        // Fallback behavior if needed
    }
}
```

### 4. Use Analytics for Click Tracking

Track important user interactions:

```kotlin
fun handleClick(clickId: String) {
    // Log the click event
    analytics.logEvent("remote_layout_click", mapOf("click_id" to clickId))

    // Then handle the click
    when (clickId) {
        "add_to_cart" -> {
            addToCart()
            // Log specific business event
            analytics.logEvent(
                "add_to_cart", mapOf(
                    "product_id" to currentProductId,
                    "price" to currentProductPrice
                )
            )
        }
        // More handlers...
    }
}
```

### 5. Test Click Handlers

Write unit tests for your click handling logic:

```kotlin
@Test
fun `when product view click received, navigation is triggered`() {
    // GIVEN
    val viewModel = ProductViewModel(mockRepository, mockNavigator)

    // WHEN
    viewModel.handleClick("view_product:123")

    // THEN
    verify(mockNavigator).navigateTo(Screen.ProductDetail("123"))
}
```

## Common Patterns

### Form Submission

Handle form submission with validation:

```kotlin
@Composable
fun DynamicForm(viewModel: FormViewModel) {
    val formState by viewModel.formState.collectAsState()
    val bindsValue = remember { BindsValue() }

    // Update binds when form state changes
    LaunchedEffect(formState) {
        bindsValue.setValue("nameValue", formState.name)
        bindsValue.setValue("emailValue", formState.email)
        bindsValue.setValue("nameError", formState.nameError ?: "")
        bindsValue.setValue("emailError", formState.emailError ?: "")
        bindsValue.setValue("isSubmitEnabled", (!formState.hasErrors).toString())
    }

    // Form layout with input fields and validation error messages
    val formLayoutJson = """
    {
      "column": {
        "modifier": {
          "base": {
            "fillMaxWidth": true,
            "padding": {
              "all": 16
            }
          }
        },
        "children": [
          {
            "text": {
              "content": "Contact Form",
              "fontSize": 24,
              "fontWeight": "bold"
            }
          },
          {
            "text": {
              "content": "Name:",
              "fontSize": 16
            }
          },
          {
            "custom_text_field": {
              "value": "{nameValue}",
              "placeholder": "Enter your name",
              "onChange": "update_name",
              "error": "{nameError}"
            }
          },
          {
            "text": {
              "content": "Email:",
              "fontSize": 16
            }
          },
          {
            "custom_text_field": {
              "value": "{emailValue}",
              "placeholder": "Enter your email",
              "onChange": "update_email",
              "error": "{emailError}"
            }
          },
          {
            "button": {
              "content": "Submit",
              "clickId": "submit_form",
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "margin": {
                    "top": 16
                  }
                }
              }
            }
          }
        ]
      }
    }
    """

    val component = createLayoutComponent(formLayoutJson)

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            when (clickId) {
                "submit_form" -> viewModel.submitForm()
                // Handle other actions...
            }
        }
    )
}
```

### Handling Multiple Selection

Implement a selection mechanism:

```kotlin
@Composable
fun SelectionExample() {
    var selectedItems by remember { mutableStateOf(setOf<String>()) }
    val bindsValue = remember { BindsValue() }

    // Items to choose from
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")

    // Update binds when selection changes
    LaunchedEffect(selectedItems) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItems.contains(item)
            bindsValue.setValue("item_${index}_selected", isSelected.toString())
            bindsValue.setValue("item_${index}_bg_color", if (isSelected) "#E3F2FD" else "#FFFFFF")
        }

        bindsValue.setValue("selected_count", "${selectedItems.size} selected")
        bindsValue.setValue("can_proceed", (selectedItems.isNotEmpty()).toString())
    }

    // Generate JSON for the selectable items
    val itemsJson = items.mapIndexed { index, item ->
        """
        {
          "card": {
            "modifier": {
              "base": {
                "fillMaxWidth": true,
                "clickId": "toggle_item:$index",
                "padding": {
                  "all": 16
                },
                "margin": {
                  "vertical": 4
                },
                "background": {
                  "color": "{item_${index}_bg_color}"
                }
              }
            },
            "children": [
              {
                "row": {
                  "modifier": {
                    "verticalAlignment": "center"
                  },
                  "children": [
                    {
                      "text": {
                        "content": "$item",
                        "fontSize": 16
                      }
                    }
                  ]
                }
              }
            ]
          }
        }
        """
    }.joinToString(",\n")

    // Selection screen layout
    val layoutJson = """
    {
      "column": {
        "modifier": {
          "base": {
            "fillMaxWidth": true,
            "padding": {
              "all": 16
            }
          }
        },
        "children": [
          {
            "text": {
              "content": "Select Items",
              "fontSize": 24,
              "fontWeight": "bold"
            }
          },
          {
            "text": {
              "content": "{selected_count}",
              "fontSize": 16,
              "color": "#666666",
              "modifier": {
                "base": {
                  "padding": {
                    "vertical": 8
                  }
                }
              }
            }
          },
          $itemsJson,
          {
            "button": {
              "content": "Continue",
              "clickId": "continue",
              "modifier": {
                "base": {
                  "fillMaxWidth": true,
                  "margin": {
                    "top": 16
                  }
                }
              }
            }
          }
        ]
      }
    }
    """

    val component = createLayoutComponent(layoutJson)

    DynamicLayout(
        component = component,
        bindValue = bindsValue,
        onClickHandler = { clickId ->
            when {
                clickId.startsWith("toggle_item:") -> {
                    val index = clickId.split(":")[1].toInt()
                    val item = items[index]

                    selectedItems = if (selectedItems.contains(item)) {
                        selectedItems - item  // Remove item if already selected
                    } else {
                        selectedItems + item  // Add item if not selected
                    }
                }
                clickId == "continue" -> {
                    if (selectedItems.isNotEmpty()) {
                        // Handle continue action with selected items
                    }
                }
            }
        }
    )
}
```

## Next Steps

Now that you understand how to handle user actions in your dynamic layouts, you can:

1. Explore the [JSON structure](../../03-json-structure/04-layout-json-structure) in detail to create
   effective layouts