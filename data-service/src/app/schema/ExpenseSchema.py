from decimal import Decimal
from typing import Optional
from pydantic import BaseModel, Field
from datetime import date as date_type

class ExpenseSchema(BaseModel):
    amount: Optional[Decimal] = Field(
        title="amount",
        description="The expense amount in decimal format. If not found, return null. (Example: '45.99')"
    )
    payee: Optional[str] = Field(
        title="payee",
        description="The name of the merchant. If not found, return null. (Example: 'Amazon')"
    )
    account: Optional[int] = Field(
        title="account",
        description="The last 4 digits of the account number. If not found, return null. (Example: '1234567890')"
    )
    txnType: Optional[str] = Field(
        title="txnType",
        description="The type of transfer ('credit', 'debit', 'transfer'). If not found, return null."
    )
    date: Optional[date_type] = Field(
        title="date",
        description="The date when the expense occurred. If not found, return null. (Example: '2025-02-18')"
    )
    category: Optional[str] = Field(
        title="category",
        description="A predefined category ('food', 'transport', 'entertainment', 'clothing', 'education', 'health', 'housing', 'bill', 'other')."
                    "If the category can be clearly identified from the text, select the appropriate category. "
                    "Else, return null. (Example: 'food')"
    )
    currency: Optional[str] = Field(
        title="currency",
        description="The currency of the expense. If not found, return null. (Example: 'USD')"
    )
