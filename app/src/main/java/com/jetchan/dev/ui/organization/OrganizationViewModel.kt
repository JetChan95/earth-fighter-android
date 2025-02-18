package com.jetchan.dev.ui.organization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetchan.dev.src.GetOrgListResponse
import com.jetchan.dev.src.JoinOrgResponse
import com.jetchan.dev.src.OrganizationBaseInfo
import com.jetchan.dev.src.ServerCommunicator
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class OrganizationViewModel : ViewModel() {

    private val _orgList = MutableLiveData<ArrayList<OrganizationBaseInfo>>()
    val orgList: LiveData<ArrayList<OrganizationBaseInfo>> = _orgList

    private val _text = MutableLiveData<String>().apply {
        value = "组织界面"
    }
    val text: LiveData<String> = _text

    fun loadData(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            ServerCommunicator.getInstance(context).getOrganizationList(object : Callback<GetOrgListResponse> {
                override fun onResponse(call: Call<GetOrgListResponse>, response: Response<GetOrgListResponse>) {
                    val orgListResponse: GetOrgListResponse? = response.body()
                    if (orgListResponse != null) {
                        Timber.d(Gson().toJson(orgListResponse))
                        _orgList.postValue(orgListResponse.data)
                    }
                }

                override fun onFailure(call: Call<GetOrgListResponse>, t: Throwable) {
                    Timber.d("error", t)
                }
            })
        }
    }

    fun joinOrganization(context: android.content.Context, orgId: Int, orgInfo: OrganizationBaseInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            ServerCommunicator.getInstance(context).joinOrganization(orgId, orgInfo, object: Callback<JoinOrgResponse> {
                override fun onResponse(call: Call<JoinOrgResponse>, response: Response<JoinOrgResponse>) {
                    val joinResponse: JoinOrgResponse? = response.body()
                    if (joinResponse != null) {
                        Timber.d(Gson().toJson(joinResponse))
                        val currentList = _orgList.value ?: arrayListOf()
                        currentList.add(joinResponse.data)
                        _orgList.postValue(currentList)
                    }
                }

                override fun onFailure(call: Call<JoinOrgResponse>, t: Throwable) {
                    Timber.d("error", t)
                }
            })
        }
    }

    fun createOrganization(context: android.content.Context, orgInfo: OrganizationBaseInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            ServerCommunicator.getInstance(context).createOrganization(orgInfo, object: Callback<OrganizationBaseInfo> {
                override fun onResponse(call: Call<OrganizationBaseInfo>, response: Response<OrganizationBaseInfo>) {
                    val org: OrganizationBaseInfo? = response.body()
                    if (org != null) {
                        Timber.d(Gson().toJson(org))
                        val currentList = _orgList.value ?: arrayListOf()
                        currentList.add(org)
                        _orgList.postValue(currentList)
                    }
                    Timber.d("msg: ${response.message()}, code: ${response.code()}, body: ${response.body()}," +
                            "raw: ${response.raw()}, errorBody: ${response.errorBody()}")
                }

                override fun onFailure(call: Call<OrganizationBaseInfo>, t: Throwable) {
                    Timber.d("error", t)
                }
            })
        }
    }
}