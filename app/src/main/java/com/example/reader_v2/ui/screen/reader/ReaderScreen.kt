package com.example.reader_v2.ui.screen.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commitNow
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
import com.example.reader_v2.data.repository.ReaderRepository
import com.example.reader_v2.ui.Screen
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubNavigatorFragment.Listener
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.AbsoluteUrl
import kotlin.jvm.java

@AndroidEntryPoint
class ReaderHostFragment : Fragment(), Listener {
	@Inject
	lateinit var readerRepository: ReaderRepository

	private val readerViewModel: ReaderViewModel by activityViewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val epubNavigatorFactory = readerRepository.navigatorFactory
			?: throw IllegalStateException("Factory not initialized")

		childFragmentManager.fragmentFactory = epubNavigatorFactory.createFragmentFactory(
			initialLocator = readerViewModel.initialLocator.value,
			listener = this
		)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return FragmentContainerView(requireContext()).apply {
			id = View.generateViewId()
		}
	}

	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)
		readerViewModel.logIdentity("Fragment")

		if (savedInstanceState == null) {
			childFragmentManager.commitNow {
				add(
					view.id,
					EpubNavigatorFragment::class.java,
					Bundle(),
					"navigator"
				)
			}
		}

		val navigatorFragment = childFragmentManager.findFragmentByTag("navigator") as? EpubNavigatorFragment
		readerRepository.navigator = navigatorFragment

	}

	override fun onDestroyView() {
		super.onDestroyView()
		readerRepository.navigator = null
	}

	@ExperimentalReadiumApi
	override fun onExternalLinkActivated(url: AbsoluteUrl) {
		TODO("Not yet implemented")
	}
}

@Composable
fun ReaderScreen(
	modifier: Modifier = Modifier,
	readerViewModel: ReaderViewModel
) {
	val isBookReady by readerViewModel.isBookReady.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		readerViewModel.logIdentity("Compose Screen")
	}

	Box(modifier = modifier.fillMaxSize()) {
		if (isBookReady) {
			AndroidFragment<ReaderHostFragment>(
				modifier = Modifier.fillMaxSize(),
				arguments = bundleOf("bookId" to readerViewModel.bookId)
			)
		} else {
			LoadingSpinner()
		}
	}
}

@Composable
fun LoadingSpinner(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
	}
}
