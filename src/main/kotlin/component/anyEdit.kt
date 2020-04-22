package component

import hoc.withDisplayName
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.functionalComponent

interface AnyEditFProps<O> : RProps {
    var objects: Array<O>
    var onClickSubmit: (Int) -> (O) -> (Event) -> Unit
    var onClickRemove: (Int) -> (Event) -> Unit
    var onClickNew: (Event) -> Unit
}

fun <O> fAnyEdit(
    rComponentEdit: RBuilder.(O, (O) -> (Event) -> Unit) -> ReactElement,
    rComponentView: RBuilder.(O, Boolean, (Event) -> Unit) -> ReactElement
) =
    functionalComponent<AnyEditFProps<O>> {
        it.objects.mapIndexed { index, obj ->
            div {
                attrs.classes = setOf("edit-container")
                div {
                    button {
                        attrs.classes = setOf("button-remove")
                        attrs.onClickFunction = it.onClickRemove(index)
                        +"x"
                    }
                    rComponentView(obj, false) {}
                }
                rComponentEdit(obj, it.onClickSubmit(index))
            }
        }
        button {
            attrs.classes = setOf("button-new-element")
            attrs.onClickFunction = it.onClickNew
            +"Add Element"
        }
    }

fun <O> RBuilder.anyEdit(
    rComponentEdit: RBuilder.(O, (O) -> (Event) -> Unit) -> ReactElement,
    rComponentView: RBuilder.(O, Boolean, (Event) -> Unit) -> ReactElement,
    objects: Array<O>,
    onClickSubmit: (Int) -> (O) -> (Event) -> Unit,
    onClickRemove: (Int) -> (Event) -> Unit,
    onClickNew: (Event) -> Unit
) = child(
    withDisplayName("Edit Elements",  fAnyEdit(rComponentEdit, rComponentView))
){
    attrs.objects = objects
    attrs.onClickSubmit = onClickSubmit
    attrs.onClickRemove = onClickRemove
    attrs.onClickNew = onClickNew
}