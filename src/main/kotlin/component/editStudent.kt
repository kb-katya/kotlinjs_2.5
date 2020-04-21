package component

import react.*
import react.dom.*
import kotlinx.html.*
import data.*
import hoc.withDisplayName
import kotlinext.js.asJsObject
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event


interface EditStudentProps : RProps {
    var student: Student
    var onClickSubmit: (Student) -> (Event) -> Unit
}

interface EditStudentState : RState {
    var isEdit: Boolean
    var student: Student
}

class EditStudent : RComponent<EditStudentProps, EditStudentState>() {

    override fun componentWillMount() {
        state.student = props.student.copy()
    }

    fun onClickEdit() = { _: Event ->
        setState {
            isEdit = !state.isEdit
            student = props.student.copy()
        }
    }

    override fun RBuilder.render() {
        if (state.isEdit) {
            input {
                attrs.type = InputType.text
                attrs.value = state.student.firstname
                attrs.placeholder = "Student firstname"
                attrs.onChangeFunction = {
                    val target = it.target?.asJsObject().unsafeCast<HTMLInputElement>()
                    setState {
                        student.firstname = target.value
                    }
                }
            }
            input {
                attrs.type = InputType.text
                attrs.value = state.student.surname
                attrs.placeholder = "Student surname"
                attrs.onChangeFunction = {
                    val target = it.target?.asJsObject().unsafeCast<HTMLInputElement>()
                    setState {
                        student.surname = target.value
                    }
                }
            }
            div {
                button {
                    +"Submit"
                    attrs.onClickFunction = props.onClickSubmit(state.student)
                }
                button {
                    +"Сancel"
                    attrs.onClickFunction = onClickEdit()
                }
            }
        } else {
            button {
                +"Edit"
                attrs.onClickFunction = onClickEdit()
            }
        }
    }

}

fun RBuilder.editStudent(
    student: Student,
    onClickSubmit: (Student) -> (Event) -> Unit
) =
    child(
        withDisplayName("Edit Student", EditStudent::class)
    ) {
        attrs.student = student
        attrs.onClickSubmit = onClickSubmit
    }