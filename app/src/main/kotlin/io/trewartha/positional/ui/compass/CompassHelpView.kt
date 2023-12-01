package io.trewartha.positional.ui.compass

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.trewartha.positional.R
import io.trewartha.positional.ui.NavDestination
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.components.HelpView
import io.trewartha.positional.ui.defaultEnterTransition
import io.trewartha.positional.ui.defaultExitTransition
import io.trewartha.positional.ui.defaultPopEnterTransition
import io.trewartha.positional.ui.defaultPopExitTransition

fun NavGraphBuilder.compassHelpView(
    navController: NavController,
    contentPadding: PaddingValues
) {
    composable(
        NavDestination.CompassHelp.route,
        enterTransition = defaultEnterTransition(),
        exitTransition = defaultExitTransition(),
        popEnterTransition = defaultPopEnterTransition(),
        popExitTransition = defaultPopExitTransition()
    ) {
        CompassHelpView(contentPadding, onUpClick = { navController.popBackStack() })
    }
}

@Composable
fun CompassHelpView(
    contentPadding: PaddingValues,
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HelpView(
        title = { Text(text = stringResource(id = R.string.compass_help_title)) },
        markdownRes = R.raw.compass_help,
        contentPadding = contentPadding,
        onUpClick = onUpClick,
        modifier = modifier
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PositionalTheme {
        CompassHelpView(PaddingValues(), onUpClick = {})
    }
}
