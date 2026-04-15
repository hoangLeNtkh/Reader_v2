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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commitNow
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.reader_v2.data.repository.ReaderRepository
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubNavigatorFragment.Listener
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.AbsoluteUrl

@AndroidEntryPoint
class ReaderHostFragment : Fragment(), Listener {
	@Inject
	lateinit var readerRepository: ReaderRepository
	private val readerViewModel: ReaderViewModel by activityViewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val navigatorFactory = readerRepository.navigatorFactory ?: throw IllegalStateException("Factory not initialized")

		childFragmentManager.fragmentFactory = navigatorFactory
			.createFragmentFactory(
				initialLocator = readerViewModel.temporaryLocator,
				listener = this
			)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		val context = requireContext()

		return FragmentContainerView(context).apply {
			id = View.generateViewId()
		}
	}

	@OptIn(ExperimentalReadiumApi::class)
	override fun onViewCreated(
		view: View,
		savedInstanceState: Bundle?
	) {
		super.onViewCreated(view, savedInstanceState)

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
		readerRepository.navigator = childFragmentManager.findFragmentByTag("navigator") as EpubNavigatorFragment

		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				readerRepository.navigator?.currentLocator?.collect {
					readerViewModel.updateTemporaryLocation(it)
				}
			}
		}
	}

	override fun onStop() {
		super.onStop()
		viewLifecycleOwner.lifecycleScope.launch {
			readerViewModel.saveReadingProgression()
		}
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
	val isBookReady by readerViewModel.isBookLoaded.collectAsStateWithLifecycle()

	Box(modifier = modifier.fillMaxSize()) {
		if (isBookReady) {
			AndroidFragment<ReaderHostFragment>(modifier = Modifier.fillMaxSize())
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
