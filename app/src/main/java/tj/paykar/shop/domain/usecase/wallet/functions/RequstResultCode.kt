package tj.paykar.shop.domain.usecase.wallet.functions

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import tj.paykar.shop.data.storage.wallet.SecurityStorage
import tj.paykar.shop.data.storage.wallet.UserStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tj.paykar.shop.MainActivity
import tj.paykar.shop.R
import tj.paykar.shop.data.storage.PaykarIdStorage
import tj.paykar.shop.data.storage.UserStorageData
import tj.paykar.shop.data.storage.wallet.SavedServicesStorage
import tj.paykar.shop.databinding.WalletBottomSheetWalletBlockedBinding
import tj.paykar.shop.presentation.authorization.LoginActivity

/**
0	Success
1	Incorrect Token
2	Wallet already exists
3	Customer not found
4	Incorrect confirmation code
5	Customer already identified
6	The service adding a card is not available
7	Awaiting identification confirmation
8	Operation not exists
9	Wrong number
10	The provider's server is unavailable
11	Error on the provider side
12	Customer not identified
13	Incorrect account
14	Insuficient Balance
15	Invalid Service Id
16	Transaction amount is less than the allowed minimum amount
17	The amount of the transaction is greater than the allowed maximum amount
18	The transaction amount exceeds the established limit per transaction
19	Daily limit exceeded
20	Weekly limit exceeded
21	Monthly limit exceeded
22	Top-up limit exceeded
25 Incorrect PIN-code
26 Incorrect Old Pin-code
27 Pin Code already used
34 Customer Blocked
100	Universal system errors
 **/

fun requestResultCodeAlert(code: Int, context: Context, description: String) {
    when(code) {
        100 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Произошла ошибка")
                .setMessage("Что-то пошло не так. Попробуйте по позже!")
                .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                .show()
        }
        15 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Произошла ошибка")
                .setMessage("Некорректная система платежа")
                .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                .show()
        }
        11 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Произошла ошибка")
                .setMessage("Ошибка на стороне провайдера")
                .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                .show()
        }
        10 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Произошла ошибка")
                .setMessage("Сервер провайдера недоступен")
                .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                .show()
        }
        6 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Произошла ошибка!")
                .setMessage("Услуга добавления карты недоступна")
                .setPositiveButton("Понятно") {a, _ -> a.cancel()}
                .show()
        }
        2 -> {
            MaterialAlertDialogBuilder(context)
                .setMessage("Кошелёк существует")
                .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                .show()
        }
        3 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Пользователь не найден!")
                .setMessage("Пользователь не зарегистрирован, зарегистрируйтесь пожалуйста!")
                .setPositiveButton("Понятно") {a, _ -> a.cancel()}
                .show()
        }
        4 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Неверный код подтверждения")
                .setMessage("Пожалуйста, убедитесь, что вы ввели правильный код и повторите попытку.")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        5 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Идентификация")
                .setMessage("Этот пользователь уже идентифицированный")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        8 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Операция")
                .setMessage("Операция не существует")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        9 -> {
            MaterialAlertDialogBuilder(context)
                .setMessage("Некорректый счёт")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        12 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Идентификация требуется")
                .setMessage("Для полного использования всех возможностей кошелька, необходимо пройти идентификацию.")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        13, 29 -> {
            MaterialAlertDialogBuilder(context)
                .setMessage("Некорректный счёт")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        14 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Выбранный счёт")
                .setMessage("У вас недостаточно средств для оплаты")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        16 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Транзакция")
                .setMessage("Сумма транзакции меньше разрешенной минимальной суммы")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        17 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Транзакция")
                .setMessage("Сумма транзакции превышает разрешенную максимальную сумму")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        18 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Транзакция")
                .setMessage("Сумма транзакции превышает установленный лимит на одну транзакцию")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        19 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Лимит транзакции")
                .setMessage("Превышен дневной лимит")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        20 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Лимит транзакции")
                .setMessage("Превышен недельный лимит")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        21 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Лимит транзакции")
                .setMessage("Превышен месячный лимит")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        22 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Лимит счёта")
                .setMessage("Превышен лимит пополнения счета")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        25, 26 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Неверный PIN-код")
                .setMessage("Пожалуйста, убедитесь, что вы ввели правильный PIN-код и повторите попытку.")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        27 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Некорректный PIN-код")
                .setMessage("Этот PIN-код уже использован. Пожалуйста, введите новый PIN-код и повторите попытку.")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        1 -> {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            UserStorageData(context).cleanStorage()
            UserStorage(context).deactivateUser()
            PaykarIdStorage(context).deactivateUser()
            SecurityStorage(context).clear()
            SavedServicesStorage(context).clearSavedServices()
        }
        7 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Идентификация")
                .setMessage("Ваша заявка на идентификацию уже отправлена")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        34 -> {
            val bottomSheetDialog = BottomSheetDialog(context)
            val bottomSheetBinding =
                WalletBottomSheetWalletBlockedBinding.inflate(LayoutInflater.from(context))
            val bottomSheetView = bottomSheetBinding.root
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()
            val customerBlocked = context.resources.getString(R.string.customerBlockedTitle)
            if (description == "Customer blocked!" || description == "Customer blocked") {
                bottomSheetBinding.descriptionTitle.text = customerBlocked
            } else {
                bottomSheetBinding.descriptionTitle.text = description
            }

            bottomSheetBinding.mainActivityBtn.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        }
        35 -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Код доступа")
                .setMessage("Вы еще не установили PIN-код")
                .setPositiveButton("Понятно") { dialog, _ -> dialog.dismiss() }
                .show()
        }
        else -> {
            MaterialAlertDialogBuilder(context)
                .setTitle("Произошла ошибка")
                .setMessage("Что-то пошло не так. Попробуйте по позже!")
                .setPositiveButton("Понятно") {dialog, _ -> dialog.cancel()}
                .show()
        }
    }
}