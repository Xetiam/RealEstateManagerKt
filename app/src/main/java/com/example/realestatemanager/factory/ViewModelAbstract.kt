package com.example.realestatemanager.factory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class ViewState<out T>


abstract class ViewModelAbstract<State : ViewState<*>> : ViewModel() {
    private val _viewState = MutableLiveData<State>()
    val viewState: LiveData<State> get() = _viewState

    protected fun setState(state: State) {
        _viewState.value = state
    }
    abstract fun initUi()
}
