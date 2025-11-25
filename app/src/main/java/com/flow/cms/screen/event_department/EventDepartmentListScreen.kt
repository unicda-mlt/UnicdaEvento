package com.flow.cms.screen.event_department

import android.widget.Toast
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.domain.entities.Department
import com.main.unicdaevento.MyAppTheme
import com.presentation.common.PopupDialogTextFieldForm
import com.presentation.common.SearchInput
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun EventDepartmentListScreen(
    navController: NavHostController,
    vm: EventDepartmentListScreenViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showPopupField by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.observeDepartments()
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is EventDepartmentListScreenViewModel.UiState.Success -> {
                Toast.makeText(
                    context,
                    (uiState as EventDepartmentListScreenViewModel.UiState.Success).message,
                    Toast.LENGTH_SHORT
                ).show()
            }
            is EventDepartmentListScreenViewModel.UiState.Error -> {
                Toast.makeText(
                    context,
                    (uiState as EventDepartmentListScreenViewModel.UiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            is EventDepartmentListScreenViewModel.UiState.Adding,
            is EventDepartmentListScreenViewModel.UiState.Updating -> {
                showPopupField = true
            }
            else -> {}
        }
    }

    if (showPopupField) {
        when(uiState) {
            is EventDepartmentListScreenViewModel.UiState.Adding -> {
                PopupDialogTextFieldForm(
                    title = "Department",
                    onSave = { name ->
                        vm.addDepartment(name)
                        showPopupField = false
                    },
                    onDismiss = { showPopupField = false }
                )
            }
            is EventDepartmentListScreenViewModel.UiState.Updating -> {
                val department = (uiState as EventDepartmentListScreenViewModel.UiState.Updating).department

                PopupDialogTextFieldForm(
                    title = "Department",
                    value = department.name,
                    onSave = { name ->
                        vm.updateDepartment(department.copy(name = name))
                        showPopupField = false
                    },
                    onDismiss = { showPopupField = false }
                )
            }
            else -> {}
        }
    }

    val onItemClick: (String, String) -> Unit = remember(navController) {
        { id, name ->
            val department = Department.create(
                id = id,
                name = name
            )
            vm.setStateUpdating(department)
        }
    }

    EventDepartmentListContent(
        params = vm.params,
        departments = vm.departments,
        updateParamSearch = vm::updateParamSearch,
        onItemClick = onItemClick,
        setStateAdding = vm::setStateAdding
    )

}

@Composable
private fun EventDepartmentListContent(
    params: ParamFlow,
    departments: DepartmentsFlow,
    updateParamSearch: (text: String) -> Unit,
    onItemClick: (id: String, name: String) -> Unit,
    setStateAdding: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(
                    top = 10.dp,
                    bottom = 15.dp,
                    start = 10.dp,
                    end = 10.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val localParams by params.collectAsStateWithLifecycle()

            SearchInput(
                modifier = Modifier.weight(1f),
                value = localParams.search,
                onValueChange = { updateParamSearch(it) },
            )

            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = setStateAdding
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(1f)
        ) {
            val localDepartments by departments.collectAsStateWithLifecycle(initialValue = emptyList())

            val listState = rememberSaveable(saver = LazyListState.Saver) {
                LazyListState()
            }

            LazyColumn(
                state = listState
            ) {
                items(
                    items = localDepartments,
                    key = { it.id },
                    contentType = { "department" }
                )
                { item ->
                    EventDepartmentListItem(
                        name = item.name,
                        onClick = { onItemClick(item.id, item.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventDepartmentListItem(
    name: String,
    onClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .bottomBorder(Color.LightGray)
            .padding(
                start = 10.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = name,
            fontSize = 20.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Box(
            modifier = Modifier
            .fillMaxHeight()
            .combinedClickable(
                onClick = onClick,
                role = Role.Button
            )
            .padding(10.dp)
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Modifier.bottomBorder(
    color: Color,
    width: Dp = 1.dp
): Modifier {
    val strokePx = with(LocalDensity.current) { width.toPx() }
    val half = strokePx / 2f

    return this.then(
        Modifier.drawBehind {
            drawLine(
                color = color,
                start = Offset(0f, size.height - half),
                end = Offset(size.width, size.height - half),
                strokeWidth = strokePx
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EventDepartmentListScreen_Preview() {
    val paramsFlow: ParamFlow = remember {
        MutableStateFlow(
            EventDepartmentListScreenViewModel.Params(
                search = ""
            )
        )
    }

    val departmentsFlow: DepartmentsFlow = remember {
        MutableStateFlow(
            listOf(
                Department(id = "1", name = "Social Sciences"),
                Department(id = "2", name = "Arts & Culture"),
                Department(id = "3", name = "Electrical Engineering Electrical Engineering")
            )
        )
    }

    MyAppTheme {
        EventDepartmentListContent(
            params = paramsFlow,
            departments = departmentsFlow,
            updateParamSearch = { },
            onItemClick = { _, _ -> },
            setStateAdding = {}
        )
    }
}