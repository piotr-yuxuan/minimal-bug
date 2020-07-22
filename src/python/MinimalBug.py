from ClojureBridge import ClojureBridge


def some_wrapped_python(*arg, **kwarg):
    print('ALL YOUR BASE ARE BELONG TO US')
    return 'yay'


# Decorator pattern
def call_me_baby(**kwargs):
    return ClojureBridge.safely()(
        lambda: some_wrapped_python(1, 2, 3, first=1, second=2, third=3))()
