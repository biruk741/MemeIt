package com.innov8.memegenerator.utils

import android.view.View
import java.util.*

class ActionManager  {
    fun hasNext(): Boolean {
        return head != null && head!!.nextAction != null
    }
    fun hasPrevious(): Boolean {
        return head != null && head!!.previousAction != null
    }

    fun next(): Action? {
        return if (hasNext()) {
            head!!.nextAction!!
        } else null
    }
    fun previous(): Action? {
        return if (hasPrevious()) {
            head!!.previousAction!!
        } else null
    }

    var head: Action? = null
    fun reset() {
        while (undo()) {
        }
    }


    fun clearHistory() {
        clearPrevious()
        head = null
    }

    fun clearPrevious() {
        head?.previousAction = null
    }
    fun clearNext(){
        head?.nextAction=null
    }

    fun getLatest():Action?{
        var l:Action?=null
        while (hasNext()){
            l=next()
        }
        return l
    }
    fun getFirst():Action?{
        var l:Action?=null
        while (hasPrevious()){
            l=previous()
        }
        return l
    }



    fun pushAction(action: Action, `do`: Boolean = true) {
        if (head == null) {
            head = action
        } else {
            head!!.nextAction = action
            action.previousAction = head
            head = action
        }
        if (`do`) `do`()
    }

    fun `do`(): Boolean = if (head != null && head!!.nextAction != null) {
        head = head!!.nextAction
        head!!.`do`()
        true
    } else false


    fun undo(): Boolean = if (head != null) {
        head!!.undo()
        head = head!!.previousAction
        true
    } else false

    fun listActionDescription(): List<String> {
        var x = getFirst()
        val list = mutableListOf<String>()
        while (x != null) {
            list += x.description
            x = x.nextAction
        }
        return list
    }

    fun isLatest() = head == null || head!!.nextAction == null
    fun isFirst() = head == null || head!!.previousAction == null


}

val empty = object : Actionable {
    override fun undo() {

    }

    override fun `do`() {
    }

}

abstract class Action(val description: String) : Actionable {
    var previousAction: Action? = null
    var nextAction: Action? = null

}

class RotationAction(val view: View, val rotate: Float) : Action("R") {
    val initialRotation = view.rotation

    override fun `do`() {
        view.rotation = rotate
    }

    override fun undo() {
        view.rotation = initialRotation
    }


}


interface Actionable {
    fun `do`()
    fun undo()
}

