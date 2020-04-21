package component

import data.*
import hoc.withDisplayName
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.router.dom.*


interface AppState : RState {
    var lessons: Array<Lesson>
    var students: Array<Student>
    var presents: Array<Array<Boolean>>
}

interface RouteNumberResult : RProps {
    var number: String
}

class App : RComponent<RProps, AppState>() {

    init {
        state.apply {
            lessons = lessonsList
            students = studentList
        }
    }

    override fun componentWillMount() {
        state.presents = Array(state.lessons.size) {
            Array(state.students.size) { false }
        }
    }

    override fun RBuilder.render() {
        header {
            h1 { +"App" }
            nav {
                ul {
                    li { navLink("/lessons") { +"Lessons" } }
                    li { navLink("/students") { +"Students" } }
                    li { navLink("/edit_lessons") { +"Edit Lessons" } }
                    li { navLink("/edit_students") { +"Edit Students" } }
                }
            }
        }

        switch {
            route("/lessons",
                exact = true,
                render = {
                    anyList(state.lessons, "Lessons", "/lessons")
                }
            )
            route("/students",
                exact = true,
                render = {
                    anyList(state.students, "Students", "/students")
                }
            )
            route("/edit_students",
                exact = true,
                render = {
                    anyEdit(RBuilder::editStudent, RBuilder::student, state.students,
                        onClickSubmitStudent, onClickRemoveStudent, onClickNewStudent)
                }
            )
            route("/edit_lessons",
                exact = true,
                render = {
                    anyEdit(RBuilder::editLesson, RBuilder::lesson, state.lessons,
                        onClickSubmitLesson, onClickRemoveLesson, onClickNewLesson)
                }
            )
            route("/lessons/:number",
                render = { route_props: RouteResultProps<RouteNumberResult> ->
                    val num = route_props.match.params.number.toIntOrNull() ?: -1
                    val lesson = state.lessons.getOrNull(num)
                    if (lesson != null)
                        anyFull(
                            RBuilder::student,
                            lesson,
                            state.students,
                            state.presents[num]
                        ) { onClick(num, it) }
                    else
                        p { +"No such lesson" }
                }
            )
            route("/students/:number",
                render = { route_props: RouteResultProps<RouteNumberResult> ->
                    val num = route_props.match.params.number.toIntOrNull() ?: -1
                    val student = state.students.getOrNull(num)
                    if (student != null)
                        anyFull(
                            RBuilder::lesson,
                            student,
                            state.lessons,
                            state.presents.map {
                                it[num]
                            }.toTypedArray()
                        ) { onClick(it, num) }
                    else
                        p { +"No such student" }
                }
            )
        }
    }

    fun onClick(indexLesson: Int, indexStudent: Int) =
        { _: Event ->
            setState {
                presents[indexLesson][indexStudent] =
                    !presents[indexLesson][indexStudent]
            }
        }

    fun <T> arrayIndexRemove(array: Array<T>, index: Int): Array<T> =
        array.filterIndexed { i, _ -> i != index }.toTypedArray()

    val onClickSubmitLesson = {
        index: Int -> {
            lesson: Lesson -> {
                _: Event -> setState { lessons[index] = lesson }
            }
        }
    }

    val onClickRemoveLesson = {
        index: Int -> {
            _: Event -> setState {
                lessons = arrayIndexRemove(state.lessons, index)
                presents = arrayIndexRemove(presents, index)
            }
        }
    }

    val onClickNewLesson = {
        _: Event -> setState {
            val arrayPresent = Array(students.size) { false }
            lessons += Lesson("Lesson ${lessons.size}")
            presents = arrayOf(*presents, arrayPresent)
        }
    }

    val onClickSubmitStudent = {
        index: Int -> {
            student: Student -> {
                _: Event -> setState { students[index] = student }
            }
        }
    }

    val onClickRemoveStudent = {
        index: Int -> {
            _: Event -> setState {
                students = arrayIndexRemove(state.students, index)
                presents = presents.map {
                    arrayIndexRemove(it, index)
                }.toTypedArray()
            }
        }
    }

    val onClickNewStudent = {
        _: Event -> setState {
            students += Student("Student ${students.size}", "")
            presents = presents.map {
                val array = it + Array(1) { false }
                array
            }.toTypedArray()
        }
    }
}

fun RBuilder.app() =
    child(
        withDisplayName("AppHoc", App::class)
    ) { }





