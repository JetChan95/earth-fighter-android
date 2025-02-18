import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrganizationDetailViewModel : ViewModel() {
    private val _orgId = MutableLiveData<String>()
    val orgId: LiveData<String> = _orgId

    private val _orgName = MutableLiveData<String>()
    val orgName: LiveData<String> = _orgName

    private val _orgType = MutableLiveData<String>()
    val orgType :LiveData<String> = _orgType

    private val _inviteCode = MutableLiveData<String>()
    val inviteCode :LiveData<String> = _inviteCode

    fun setOrganizationData(id: String = "--",
                            name: String = "--",
                            type: String = "--",
                            inviteCode: String = "--") {
        _orgId.value = id
        _orgName.value = name
        _orgType.value = type
        _inviteCode.value = inviteCode
    }
}