package com.itunessearchandplay.itunessearchandplay.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.itunessearchandplay.itunessearchandplay.R

/**
 * Author: Andreas Pribitzer
 */

class AddToFavoritesDialog: DialogFragment() {

    companion object {
        private const val KEY_POSITION = "position"

        fun newInstance(position: Int): AddToFavoritesDialog {
            val fragment =
                AddToFavoritesDialog()
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private var listener: IAddToFavorites? = null
    fun setListener(listener: IAddToFavorites) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val position = arguments?.getInt(KEY_POSITION)

        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.favorites)
            .setMessage(getString(R.string.add_to_favorites))
            .setPositiveButton(android.R.string.yes) {_,_->
                if(listener != null && position != null) listener!!.addToFavorites(position)
            }
            .setNegativeButton(android.R.string.no) {_,_-> }
        return builder.create()
    }

    interface IAddToFavorites {
        fun addToFavorites(position: Int)
    }
}