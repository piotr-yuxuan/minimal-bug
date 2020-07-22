def local_fallback(thunk):
    print('safely when run from Clojure')
    return thunk()


"""safely_clj is set from Clojure"""
safely_clj = local_fallback


def safely():
    def wrapper(func):
        def inner(*args, **kwargs):
            def thunk():
                ret = func(*args, **kwargs)
                print('Python after')
                return ret

            return safely_clj(thunk)

        return inner

    return wrapper
