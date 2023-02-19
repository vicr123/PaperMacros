import Styles from './App.module.css';

import MacroList from "./macros/macroList";

function App() {
    const searchParams = new URLSearchParams(window.location.search);
    if (!searchParams.has("auth")) {
        return <div className={Styles.App}>
            <div className={Styles.Error}>
                <span className={Styles.ErrorTitle}>Error</span>
                <span>Please open this page using the /edmacro command.</span>
            </div>
        </div>
    }

    return <div className={Styles.App}>
        <MacroList />
    </div>
}

export default App;
