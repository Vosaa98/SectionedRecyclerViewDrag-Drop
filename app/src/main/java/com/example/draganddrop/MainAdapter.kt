package com.example.draganddrop

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.draganddrop.databinding.FooterBinding
import com.example.draganddrop.databinding.HeaderBinding
import com.example.draganddrop.databinding.RowBinding

class MainAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val viewTypeHeader = 0
    val viewTypeFooter = 1
    private val viewTypeItem = 2

    var itemList = ArrayList<String>()
    private var headerList = ArrayList<Int>()
    private var footerList = ArrayList<Int>()

    private var headerCounter = 0
    private var footerCounter = 0

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(
        newItemList: ArrayList<String>,
        newHeaderList: ArrayList<Int>,
        newFooterList: ArrayList<Int>
    ) {
        itemList = newItemList
        headerList = newHeaderList
        footerList = newFooterList
        notifyDataSetChanged()
    }

    class ItemViewHolder private constructor(private val binding: RowBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bindItem(text: String, position: Int) {
            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "$position", Toast.LENGTH_SHORT).show()
            }
            binding.rowTextView.text = text
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }

    class HeaderViewHolder private constructor(private val binding: HeaderBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bindHeader(header: String, position: Int) {
            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "$position", Toast.LENGTH_SHORT).show()
            }
            binding.rowTextView.text = header
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(binding)
            }
        }
    }

    class FooterViewHolder private constructor(private val binding: FooterBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bindFooter(footer: String, position: Int) {
            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "$position", Toast.LENGTH_SHORT).show()
            }
            binding.rowTextView.text = footer
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FooterBinding.inflate(layoutInflater, parent, false)
                return FooterViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeItem -> {
                //Inflate Item
                ItemViewHolder.from(parent)
            }
            viewTypeHeader -> {
                //Inflate Header
                HeaderViewHolder.from(parent)
            }
            else -> {
                //Inflate Footer
                FooterViewHolder.from(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.bindItem(itemList[position], position)
                //cast holder to VHItem and set data
            }
            is HeaderViewHolder -> {
                holder.bindHeader("Header $headerCounter", position)
                //cast holder to VHHeader and set data for header.
            }
            is FooterViewHolder -> {
                holder.bindFooter("Footer $footerCounter", position)
                //cast holder to VHHeader and set data for header.
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when {
            isPositionHeader(position) -> viewTypeHeader
            isPositionFooter(position) -> viewTypeFooter
            else -> viewTypeItem
        }
    }

    private fun isPositionHeader(position: Int): Boolean {
        return if (headerList.contains(position)) {
            headerCounter = headerList.indexOf(position) + 1
            true
        } else false
    }

    private fun isPositionFooter(position: Int): Boolean {
        return if (footerList.contains(position)) {
            footerCounter = footerList.indexOf(position) + 1
            true
        } else false
    }

    fun updateHeader(sourcePosition: Int, targetPosition: Int, newItemPosition: Int) {

        val indexH = headerList.indexOf(targetPosition)
        headerList[indexH] = sourcePosition

        val indexF = footerList.indexOf(newItemPosition)
        footerList[indexF] = targetPosition
    }

    fun updateFooter(sourcePosition: Int, targetPosition: Int, newItemPosition: Int) {

        val indexH = headerList.indexOf(newItemPosition)
        headerList[indexH] = targetPosition

        val indexF = footerList.indexOf(targetPosition)
        footerList[indexF] = sourcePosition
    }

    fun adapterDeleteItem(position: Int) {
        itemList.removeAt(position)
        for (i in 0 until footerList.size) {

            if (footerList[i] > position) {
                footerList[i] -= 1
            }
            if (headerList[i] > position) {
                headerList[i] -= 1
            }
        }
        notifyItemRemoved(position)
    }

    fun deleteList(headerPosition: Int, footerPosition: Int) {
        for (i in 0 until headerList.size) {
            if (headerList[i] > headerPosition) {
                headerList[i] -= 2
            }
            if (footerList[i] > footerPosition) {
                footerList[i] -= 2
            }
        }
        headerList.remove(headerPosition)
        footerList.remove(footerPosition)

        itemList.removeAt(headerPosition)
        itemList.removeAt(footerPosition)

        notifyDataSetChanged()
    }

}