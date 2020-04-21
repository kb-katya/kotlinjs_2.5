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


interface EditLessonProps : RProps {
    var lesson: Lesson
    var onClickSubmit: (Lesson) -> (Event) -> Unit
}

interface EditLessonState : RState {
    var isEdit: Boolean
    var lesson: Lesson
}

class EditLesson : RComponent<EditLessonProps, EditLessonState>() {

    override fun componentWillMount() {
        state.lesson = props.lesson.copy()
    }

    fun onClickEdit() = { _: Event ->
        setState {
            isEdit = !state.isEdit
            lesson = props.lesson.copy()
        }
    }

    override fun RBuilder.render() {
        if (state.isEdit) {
            input {
                attrs.type = InputType.text
                attrs.value = state.lesson.name
                attrs.placeholder = "Lecture title"
                attrs.onChangeFunction = {
                    val target = it.target?.asJsObject().unsafeCast<HTMLInputElement>()
                    setState {
                        lesson.name = target.value
                    }
                }
            }
            div {
                button {
                    +"Submit"
                    attrs.onClickFunction = props.onClickSubmit(state.lesson)
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

fun RBuilder.editLesson(
    lesson: Lesson,
    onClickSubmit: (Lesson) -> (Event) -> Unit
) =
    child(
        withDisplayName("Edit Lesson", EditLesson::class)
    ) {
        attrs.lesson = lesson
        attrs.onClickSubmit = onClickSubmit
    }