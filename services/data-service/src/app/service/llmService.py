from langchain.output_parsers import PydanticOutputParser
from langchain_core.prompts import PromptTemplate
from langchain_mistralai import ChatMistralAI
from dotenv import load_dotenv
from schema.ExpenseSchema import ExpenseSchema
import os


class LLMService:
    def __init__(self):
        load_dotenv()
        self.parser=parser = PydanticOutputParser(pydantic_object=ExpenseSchema)
        self.prompt = PromptTemplate(
            template=
              """Extract structured expense data from the text with leading or trailing spaces for each data." 
                'Example:
                Input: "Dear User A/C X8989 debited by Rs 45.99 at MTR Hotel on 2025-02-18 at 2:23 PM."
                Output: {{
                    "amount": 45.99,
                    "payee": "MTR Hotel",
                    "account": 8989,
                    "txnType": "debit",
                    "date": "2025-02-18",
                    "category": "food",
                    "currency": "INR"
                }}
                
                Edge Case Example:
                Input: "Dear User, your A/C X2121 credited by Rs 25,000 on 19Jun24 from Ram Pal -UPI ."
                Output: {{
                    "amount": 25000.00,
                    "payee": "Ram Pal",
                    "txnType": "credit",
                    "account": 2121,
                    "date": null,
                    "category": null,
                    "currency": "INR"
                }}
                \n{format_instructions}\n{query}\n""",
            input_variables=["query"],
            partial_variables={"format_instructions": parser.get_format_instructions()},
        )

        self.api_key = os.getenv("MISTRALAI_KEY")
        self.llm = ChatMistralAI(api_key=self.api_key, model_name="mistral-large-latest", temperature=0)
        self.runnable = self.prompt | self.llm

    def runLLM(self, text: str):
        response = self.runnable.invoke({"query": text})
        if hasattr(response, 'content'):
            response_text = response.content
        else:
            response_text = str(response)
        return self.parser.parse(response_text)
