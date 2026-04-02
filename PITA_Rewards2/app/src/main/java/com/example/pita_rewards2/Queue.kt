package com.example.pita_rewards2

data class Queue(
    var id: Int,
    val customerName: String,
    val items: List<String>,
    var next: Queue? = null,
    var position: Int = 0
)

object QueueManager {
    var head: Queue? = null
    var tail: Queue? = null
    var nextId = 1

    fun submitOrder(customerName: String, items: List<String>): Queue {
        val newOrder = Queue(nextId++, customerName, items)
        if (head == null) {
            head = newOrder
            tail = newOrder
        } else {
            tail!!.next = newOrder
            tail = newOrder
        }
        updatePositions()
        return newOrder
    }
    fun finishOrder() {
        if (head != null) {
            head = head!!.next
            if (head == null) {
                tail = null
            }
            updatePositions()
        }
    }
    fun updatePositions() {
        var current = head
        var pos = 1
        while (current != null) {
            current.position = pos
            current = current.next
            pos++
        }
    }
    //fun getHead(): Queue? = head

    fun getQueueList(): List<Queue> {
        val lst = mutableListOf<Queue>()
        var current = head
        while (current != null) {
            lst.add(current)
            current = current.next
        }
        return lst
    }
}