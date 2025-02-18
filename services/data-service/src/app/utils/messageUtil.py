import re

class MessageUtil:
    def is_bank_sms(self, message):
        words_to_search = ["spend", "card", "bank",  "transaction", "payment", "debit", "credit", "balance", "withdrawal", "deposit", "transfer", "statement", "account", "alert", "confirmation"]
        pattern = r'(?:' + '|'.join(re.escape(keyword) for keyword in words_to_search) + r')'
        return bool(re.search(pattern, message, flags=re.IGNORECASE))