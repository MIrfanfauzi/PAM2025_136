package com.example.rotibox.ui.auth.daftar

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rotibox.ui.ViewModelFactory
import com.example.rotibox.ui.components.RotiBoxButton
import com.example.rotibox.ui.components.RotiBoxTextField

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rotibox.RotiBoxApplication
import com.example.rotibox.databinding.FragmentHalamanDaftarBinding



/**
 * Register Screen - Composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: DaftarViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val registerResult by viewModel.registerResult.observeAsState()

    // Handle register result
    LaunchedEffect(registerResult) {
        when (val result = registerResult) {
            is DaftarViewModel.RegisterResult.Success -> {
                isLoading = false
                showSuccessDialog = true
            }

            is DaftarViewModel.RegisterResult.Error -> {
                isLoading = false
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
            }

            null -> { /* Initial state */
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Tidak bisa dismiss dengan tap di luar */ },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            },
            title = {
                Text(
                    text = "Registrasi Berhasil!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Akun Anda telah berhasil dibuat. Silakan login untuk melanjutkan.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateToLogin()
                    }
                ) {
                    Text("Ke Halaman Login")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Akun") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Isi data di bawah untuk mendaftar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Name Field
            RotiBoxTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nama Lengkap",
                placeholder = "Masukkan nama lengkap",
                leadingIcon = Icons.Default.Person,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            RotiBoxTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Masukkan email",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Field
            RotiBoxTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Nomor Telepon",
                placeholder = "Masukkan nomor telepon",
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address Field (NEW)
            RotiBoxTextField(
                value = address,
                onValueChange = { address = it },
                label = "Alamat",
                placeholder = "Masukkan alamat lengkap",
                leadingIcon = Icons.Default.Home,
                imeAction = ImeAction.Next,
                enabled = !isLoading,
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            RotiBoxTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Minimal 6 karakter",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            RotiBoxTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Konfirmasi Password",
                placeholder = "Masukkan ulang password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if (name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() &&
                        address.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
                    ) {
                        isLoading = true
                        viewModel.register(name, email, phone, address, password, confirmPassword)
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Register Button
            RotiBoxButton(
                text = "Daftar",
                onClick = {
                    isLoading = true
                    viewModel.register(name, email, phone, address, password, confirmPassword)
                },
                enabled = !isLoading,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(
                    onClick = onNavigateToLogin,
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Masuk",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}




/**
 * Fragment untuk halaman daftar (register)
 */
class HalamanDaftar : Fragment() {
    
    private var _binding: FragmentHalamanDaftarBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DaftarViewModel by viewModels {
        val app = requireActivity().application as RotiBoxApplication
        val database = app.database
        val authRepository = com.example.rotibox.data.repository.AuthRepository(database.userDao())
        val menuRepository = com.example.rotibox.data.repository.MenuRepository(database.menuDao())
        val keranjangRepository = com.example.rotibox.data.repository.KeranjangRepository(database.cartItemDao())
        val pesananRepository = com.example.rotibox.data.repository.PesananRepository(database.orderDao(), database.orderItemDao())
        val laporanRepository = com.example.rotibox.data.repository.LaporanRepository(database.orderDao())
        val infoContactRepository = com.example.rotibox.data.repository.InfoContactRepository(database.infoContactDao())

        ViewModelFactory(authRepository, menuRepository, keranjangRepository, pesananRepository, laporanRepository, infoContactRepository)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHalamanDaftarBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
        observeViewModel()
    }
    
    private fun setupListeners() {
        binding.btnDaftar.setOnClickListener {
            val name = binding.etNama.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()
            val address = binding.etAlamat.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etKonfirmasiPassword.text.toString()
            
            viewModel.register(name, email, phone, address, password, confirmPassword)
        }
        
        binding.tvLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DaftarViewModel.RegisterResult.Success -> {
                    Toast.makeText(requireContext(), "Pendaftaran berhasil! Silakan login", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is DaftarViewModel.RegisterResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
