package com.example.storyvision_client
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.storyvision_client.data.AuthRepository
import com.example.storyvision_client.data.entities.EntitiesRepository
import com.example.storyvision_client.data.importdata.ImportRepository
import com.example.storyvision_client.data.local.TokenStorage
import com.example.storyvision_client.data.remote.NetworkModule
import com.example.storyvision_client.ui.account.AccountScreen
import com.example.storyvision_client.ui.account.AccountViewModel
import com.example.storyvision_client.ui.account.AccountViewModelFactory
import com.example.storyvision_client.ui.auth.AuthViewModel
import com.example.storyvision_client.ui.auth.AuthViewModelFactory
import com.example.storyvision_client.ui.auth.LoginScreen
import com.example.storyvision_client.ui.auth.RegisterScreen
import com.example.storyvision_client.ui.main.ProjectScreen
import com.example.storyvision_client.ui.navigation.AccountRoute
import com.example.storyvision_client.ui.navigation.LoginRoute
import com.example.storyvision_client.ui.navigation.ProjectListRoute
import com.example.storyvision_client.ui.navigation.ProjectRoute
import com.example.storyvision_client.ui.navigation.RegisterRoute
import com.example.storyvision_client.ui.projects.ProjectListScreen
import com.example.storyvision_client.ui.projects.ProjectListViewModel
import com.example.storyvision_client.ui.projects.ProjectListViewModelFactory
import com.example.storyvision_client.ui.theme.AppTheme
import androidx.core.content.edit


fun debugStack(backStack: MutableList<Any>, source: String) {
    println("NAVIGATION [$source]: backStack = ${backStack.toList()}")
}
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = NetworkModule.provideAuthApi()
        val entitiesApi = NetworkModule.provideEntitiesApi()

        val importApi = NetworkModule.provideImportApi() // добавить в NetworkModule, по аналогии с provideEntitiesApi

        val tokenStorage = TokenStorage(this)
        val repository = AuthRepository(api, tokenStorage)
        val importRepository = ImportRepository(importApi, repository)
        val entitiesRepository = EntitiesRepository(entitiesApi, repository)
        val accessToken = tokenStorage.getAccessToken()
        val refreshToken = tokenStorage.getRefreshToken()
        val startKey: Any = if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
            ProjectListRoute
        } else {
            LoginRoute
        }
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)


        setContent {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(repository)
            )
            val accountViewModel: AccountViewModel = viewModel(
                factory = AccountViewModelFactory(repository)
            )
            val projectListViewModel: ProjectListViewModel = viewModel(
                factory = ProjectListViewModelFactory(repository)
            )
            val backStack = remember { mutableStateListOf<Any>(startKey) }
            debugStack(backStack, "1")
            println("Custom: $startKey, $accessToken, $refreshToken")
            var isDarkTheme by remember {
                mutableStateOf(prefs.getBoolean("dark_theme", false))
            }
            AppTheme(darkTheme = isDarkTheme) {
                Surface {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = { key ->
                            when (key) {
                                is LoginRoute -> NavEntry(key) {
                                    LoginScreen(
                                        viewModel = authViewModel,
                                        onNavigateToRegister = {
                                            backStack.add(RegisterRoute)
                                        },
                                        onLoginSuccess = {
                                            backStack.clear()
                                            backStack.add(ProjectListRoute)
                                        }
                                    )
                                }

                                is RegisterRoute -> NavEntry(key) {
                                    RegisterScreen(
                                        viewModel = authViewModel,
                                        onNavigateBackToLogin = {
                                            backStack.removeLastOrNull()
                                        },
                                        onRegisterSuccess = {
                                            backStack.clear()
                                            backStack.add(ProjectListRoute)
                                        }
                                    )
                                }

                                is ProjectListRoute -> NavEntry(key) {
                                    ProjectListScreen(
                                        viewModel = projectListViewModel,
                                        onOpenProject = { project ->
                                            backStack.add(
                                                ProjectRoute(
                                                    id = project.id,
                                                    name = project.name
                                                )
                                            )
                                        },
                                        onUnauthorized = {
                                            // Очищаем стек и переходим на Login
                                            backStack.clear()
                                            backStack.add(LoginRoute)
                                        },
                                        onOpenAccount = {
                                            backStack.add(AccountRoute)
                                        },
                                        onThemeChange = { newValue ->
                                            isDarkTheme = newValue
                                            prefs.edit { putBoolean("dark_theme", newValue) }
                                        },
                                        isDarkTheme = isDarkTheme
                                    )
                                }



                                is ProjectRoute -> NavEntry(key) {
                                    ProjectScreen(
                                        projectId = key.id,
                                        projectName = key.name,
                                        entitiesRepo = entitiesRepository,
                                        importRepo = importRepository,
                                        isDarkTheme = isDarkTheme,
                                        onThemeChange = { newValue ->
                                            isDarkTheme = newValue
                                            prefs.edit { putBoolean("dark_theme", newValue) }
                                        },
                                        onOpenAccount = { backStack.add(AccountRoute) },
                                        onUnauthorized = {
                                            backStack.clear()
                                            backStack.add(LoginRoute)
                                        }
                                    )
                                }

                                is AccountRoute -> NavEntry(key) {
                                    AccountScreen(
                                        viewModel = accountViewModel,
                                        onBackToLogin = {
                                            backStack.clear()
                                            backStack.add(LoginRoute)
                                        },
                                        getBack = {
                                            backStack.remove(AccountRoute)
                                        }
                                    )
                                }

                                else -> NavEntry(Unit) {
                                    androidx.compose.material3.Text("Unknown route")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}