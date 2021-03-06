package com.innov8.memeit.commons


abstract class AdapterHandler<A : ELEAdapter<*>>(val adapter: A) {

    init {
        adapter.onErrorAction = { refresh(true) }
        if (adapter.onEmptyAction == null)
            adapter.onEmptyAction = { refresh(true) }
        adapter.onLoadMore = { load() }
    }

    fun load() {
        adapter.loading = true
        onLoad(false)
    }

    fun refresh(showLoading: Boolean = false) {
        adapter.loading = showLoading
        onLoad(true)
    }

    protected abstract fun onLoad(refresh: Boolean)


    protected fun beforeLoaded() {
        adapter.loading = false
    }

    protected fun afterLoaded(hasMore: Boolean = true) {
        onLoaded?.invoke()
        if (adapter.getCount() == 0) adapter.mode = ELEAdapter.MODE_EMPTY
        adapter.hasMore = hasMore
    }

    fun onLoadFailed(error: String) {
        adapter.loading = false
        onLoadFailed?.invoke(error)
        if (adapter.getCount() == 0) adapter.mode = ELEAdapter.MODE_ERROR
    }

    var onLoaded: (() -> Unit)? = null
    var onLoadFailed: ((String) -> Unit)? = null


}

class LoaderAdapterHandler<T : Any>(eleListAdapter: ELEListAdapter<T, *>,
                                    var loader: Loader<out T>,
                                    val limit: Int = 20)
    : AdapterHandler<ELEListAdapter<T, *>>(eleListAdapter) {


    override fun onLoad(refresh: Boolean) {
        if (refresh) loader.reset()
        loader.load(limit, {
            beforeLoaded()
            if (refresh) adapter.setAll(it) else adapter.addAll(it)
            afterLoaded(it.size >= limit)
            loader.incSkip(it.size)
        }, {
            onLoadFailed(it)
        })
    }
}

class FilterableLoaderAdapterHandler<T : Any>(eleListAdapter: ELEFilterableListAdapter<T, *>,
                                              var loader: Loader<out T>,
                                              val limit: Int = 20)
    : AdapterHandler<ELEFilterableListAdapter<T, *>>(eleListAdapter) {


    override fun onLoad(refresh: Boolean) {
        if (refresh) loader.reset()
        loader.load(limit, {
            beforeLoaded()
            adapter.addAll(it)
            afterLoaded(it.size >= limit)
            loader.incSkip(it.size)
        }, {
            onLoadFailed(it)
        })
    }
}