import os
import sys

print(f"Number of arguments: {len(sys.argv)}, arguments.")
print(f"Argument List: {str(sys.argv)}")
print(os.environ['ENV_VAR_TO_PRINT'])
print(os.environ['ENV_VAR_TO_PRINT_2'])
