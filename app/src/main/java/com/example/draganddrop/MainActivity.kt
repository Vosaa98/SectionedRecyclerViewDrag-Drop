package com.example.draganddrop

import android.content.res.TypedArray
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.draganddrop.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var list = ArrayList<ArrayList<String>>()
    private var itemList = ArrayList<String>()
    private var headerList = ArrayList<Int>()
    private var footerList = ArrayList<Int>()

    private lateinit var adapter: MainAdapter

    private lateinit var touchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        createDummyList()
        createTouchHelper()
        initAdapter()
    }

    private fun createDummyList() {
        val itemListOne = ArrayList<String>()
        val itemListTwo = ArrayList<String>()
        val itemListThree = ArrayList<String>()
        val itemListFour = ArrayList<String>()

        val arrayText: TypedArray = resources.obtainTypedArray(R.array.restext)
        for (i in 0 until arrayText.length()) {
            val string = arrayText.getString(i)
            when {
                i <= 1 -> itemListOne.add(string.toString())
                i <= 7 -> itemListTwo.add(string.toString())
                i <= 9 -> itemListThree.add(string.toString())
                else -> itemListFour.add(string.toString())
            }
        }
        list.add(itemListOne)
        list.add(itemListTwo)
        list.add(itemListThree)
        list.add(itemListFour)

        arrayText.recycle()
    }

    private fun createTouchHelper() {
        touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.START or ItemTouchHelper.END,
             ItemTouchHelper.RIGHT
        ) {

            //Scroll speed while dragging

//            override fun interpolateOutOfBoundsScroll(
//                recyclerView: RecyclerView,
//                viewSize: Int,
//                viewSizeOutOfBounds: Int,
//                totalSize: Int,
//                msSinceStartScroll: Long
//            ): Int {
//                val direction = sign(viewSizeOutOfBounds.toFloat()).toInt()
//                return 10 * direction
//            }


            //Disable drag for header and footer
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Check here whatever you want, return 0 if you want disable swipe.
                if (viewHolder.itemViewType == adapter.viewTypeHeader || viewHolder.itemViewType == adapter.viewTypeFooter) return 0
                return super.getMovementFlags(recyclerView, viewHolder)
            }

            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = p1.absoluteAdapterPosition
                val targetPosition = p2.absoluteAdapterPosition

                //When dragging to fast, it causes bugs
                //So we have to limit to swapping items only if the TARGET item is next to the SOURCE item
                if (targetPosition + 1 != sourcePosition && targetPosition - 1 != sourcePosition) {
                    return true
                }

                //When item is dragged over a HEADER or FOOTER
                when (p2) {
                    is MainAdapter.HeaderViewHolder -> {
                        //***** HEADER *****
                        //If it is the 1st Header of the list, return
                        if (targetPosition == 0) return true

                        val newItemPosition = targetPosition - 1

                        //When dragging over a header we need to update both header and footer
                        adapter.updateHeader(sourcePosition, targetPosition, newItemPosition)
                        Collections.swap(
                            adapter.itemList,
                            sourcePosition,
                            targetPosition
                        )
                        Collections.swap(
                            adapter.itemList,
                            targetPosition,
                            newItemPosition
                        )

                        adapter.notifyItemMoved(sourcePosition, targetPosition)
                        adapter.notifyItemMoved(targetPosition, newItemPosition)

                        //                    adapter.updateHeader(sourcePosition, targetPosition)
                        //                    adapter.updateFooter(targetPosition, newItemPosition)
                    }
                    is MainAdapter.FooterViewHolder -> {
                        //***** FOOTER *****
                        //If it is the last Footer of the list, return
                        if (targetPosition == itemList.size - 1) return true

                        val newItemPosition = targetPosition + 1

                        //When dragging over a header we need to update both header and footer
                        adapter.updateFooter(sourcePosition, targetPosition, newItemPosition)
                        Collections.swap(
                            adapter.itemList,
                            sourcePosition,
                            targetPosition
                        )
                        Collections.swap(
                            adapter.itemList,
                            targetPosition,
                            newItemPosition
                        )

                        adapter.notifyItemMoved(sourcePosition, targetPosition)
                        adapter.notifyItemMoved(targetPosition, newItemPosition)
                    }
                    else -> {
                        Collections.swap(
                            adapter.itemList,
                            sourcePosition,
                            targetPosition
                        )
                        adapter.notifyItemMoved(sourcePosition, targetPosition)
                    }
                }

                return true
            }

            override fun onSwiped(holder: RecyclerView.ViewHolder, swipeIndicator: Int) {
                if (swipeIndicator == ItemTouchHelper.RIGHT) {
                    adapter.adapterDeleteItem(holder.absoluteAdapterPosition)
                    Toast.makeText(this@MainActivity, "You swiped RIGHT. Item deleted.", Toast.LENGTH_SHORT).show()
                }
            }
        })

        touchHelper.attachToRecyclerView(binding.recycler)
    }

    private fun initAdapter() {
        adapter = MainAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
        createAdapterList(list)
    }

    private fun createAdapterList(newList: ArrayList<ArrayList<String>>) {
        //First values of header and footer
        var header = 0
        var footer = newList[0].size + 1

        newList.forEach {
            if (newList.indexOf(it) != 0) {
                val index = newList.indexOf(it) - 1
                header += newList[index].size + 2
                footer += it.size + 2
            }
            footerList.add(footer)
            headerList.add(header)
            itemList.addAll(it)

            //Add dummy item on HEADER positions
            itemList.add(header, "DUMMY HEADER")
            itemList.add(footer, "DUMMY Footer")
        }
        adapter.submitList(itemList, headerList, footerList)

    }

}